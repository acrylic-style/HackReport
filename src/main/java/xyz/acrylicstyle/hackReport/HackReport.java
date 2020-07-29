package xyz.acrylicstyle.hackReport;

import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import util.Collection;
import util.CollectionList;
import util.ICollectionList;
import xyz.acrylicstyle.api.MojangAPI;
import xyz.acrylicstyle.hackReport.commands.CommandLogCommand;
import xyz.acrylicstyle.hackReport.commands.HackReportCommand;
import xyz.acrylicstyle.hackReport.commands.IgnoreCommand;
import xyz.acrylicstyle.hackReport.commands.MuteAllCommand;
import xyz.acrylicstyle.hackReport.commands.MuteCommand;
import xyz.acrylicstyle.hackReport.commands.NameChangesCommand;
import xyz.acrylicstyle.hackReport.commands.OpChatCommand;
import xyz.acrylicstyle.hackReport.commands.PlayerCommand;
import xyz.acrylicstyle.hackReport.commands.ReportCommand;
import xyz.acrylicstyle.hackReport.commands.ReportsCommand;
import xyz.acrylicstyle.hackReport.commands.WarnCommand;
import xyz.acrylicstyle.hackReport.utils.PlayerInfo;
import xyz.acrylicstyle.hackReport.utils.ReportDetails;
import xyz.acrylicstyle.hackReport.utils.Utils;
import xyz.acrylicstyle.hackReport.utils.Webhook;
import xyz.acrylicstyle.tomeito_api.TomeitoAPI;
import xyz.acrylicstyle.tomeito_api.providers.ConfigProvider;
import xyz.acrylicstyle.tomeito_api.sounds.Sound;
import xyz.acrylicstyle.tomeito_api.utils.Log;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HackReport extends JavaPlugin implements Listener {
    public static final CollectionList<UUID> opChat = new CollectionList<>();
    public static final CollectionList<UUID> commandLog = new CollectionList<>();
    public static final Collection<UUID, PlayerInfo> PLAYERS = new Collection<>();
    public static final CollectionList<ReportDetails> REPORTS = new CollectionList<>();
    public static final Collection<UUID, String> warnQueue = new Collection<>();
    public static ConfigProvider config = null;
    public static boolean muteAll = false;

    public static LuckPerms luckPerms = null;

    private static HackReport instance;

    public static HackReport getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;
    }

    @NotNull
    public static PlayerInfo getPlayerInfo(@NotNull String name, @NotNull UUID uuid) {
        if (!PLAYERS.containsKey(uuid)) PLAYERS.add(uuid, new PlayerInfo(name, uuid));
        return PLAYERS.get(uuid);
    }

    @Override
    public void onEnable() {
        Log.info("Loading configuration");
        config = new ConfigProvider("./plugins/HackReport/config.yml");
        config.reload(); // ???
        Log.info("Registering commands");
        TomeitoAPI.registerCommand("hackreport", new HackReportCommand());
        TomeitoAPI.registerCommand("player", new PlayerCommand());
        TomeitoAPI.registerCommand("report", new ReportCommand());
        TomeitoAPI.registerCommand("reports", new ReportsCommand());
        TomeitoAPI.registerCommand("ignore", new IgnoreCommand());
        TomeitoAPI.registerCommand("mute", new MuteCommand());
        TomeitoAPI.registerCommand("opchat", new OpChatCommand());
        TomeitoAPI.registerCommand("commandlog", new CommandLogCommand());
        TomeitoAPI.registerCommand("muteall", new MuteAllCommand());
        TomeitoAPI.registerCommand("namechanges", new NameChangesCommand());
        TomeitoAPI.registerCommand("warn", new WarnCommand());
        Log.info("Registering events");
        Bukkit.getPluginManager().registerEvents(this, this);
        new BukkitRunnable() {
            @Override
            public void run() {
                RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
                if (provider != null) luckPerms = provider.getProvider();
            }
        }.runTaskLater(this, 1);
        new BukkitRunnable() {
            @Override
            public void run() {
                warnQueue.forEach((uuid, warn) -> {
                     Player player = Bukkit.getPlayer(uuid);
                     if (player == null) return;
                     player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 100F, 0F);
                     player.sendMessage("");
                     player.sendMessage(ChatColor.GOLD + "===============================");
                     player.sendMessage("");
                     player.sendMessage(ChatColor.RED + "Adminからの警告があります。");
                     player.sendMessage(ChatColor.RED + "内容/理由: " + warn);
                     player.sendMessage("");
                     player.sendMessage(ChatColor.GOLD + "===============================");
                     player.sendMessage("");
                     warnQueue.remove(uuid);
                });
            }
        }.runTaskTimer(this, 20, 20);
        Log.info("Enabled HackReport");
    }

    @SuppressWarnings("unchecked")
    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent e) {
        if (Bukkit.getOfflinePlayer(e.getUniqueId()).isOp()) return;
        String ip = e.getAddress().getHostAddress();
        Object o = config.get("ips." + ip);
        if (o == null) return;
        Map<String, Object> map = ConfigProvider.getConfigSectionValue(o, true);
        if (map == null) return;
        boolean kickPlayer = (boolean) map.get("kickPlayer");
        CollectionList<String> kickMessage = ICollectionList.asList((List<String>) map.get("kickMessage")).map(s -> ChatColor.translateAlternateColorCodes('&', s));
        if (kickPlayer) e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, kickMessage.join("\n"));
    }

    @SuppressWarnings("unchecked")
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (!e.getPlayer().isOp()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    CollectionList<String> list = new CollectionList<>();
                    MojangAPI.getNameChanges(e.getPlayer().getUniqueId()).reverse().foreach((history, index) -> {
                        if (index >= 1 && index < 4) {
                            list.add(history.getName());
                        }
                    });
                    if (list.size() == 0) return;
                    Bukkit.broadcastMessage(ChatColor.GRAY + "(also known as " + list.join(", ") + ")");
                }
            }.runTaskLaterAsynchronously(this, 1);
        }
        String ip = e.getPlayer().getAddress().getAddress().getHostAddress();
        Object o = config.get("ips." + ip);
        if (o == null) return;
        Map<String, Object> map = ConfigProvider.getConfigSectionValue(o, true);
        if (map == null) return;
        boolean kickPlayer = (boolean) map.get("kickPlayer");
        CollectionList<String> kickMessage = ICollectionList.asList((List<String>) map.get("kickMessage")).map(s -> ChatColor.translateAlternateColorCodes('&', s));
        if (e.getPlayer().isOp() && kickPlayer) {
            e.getPlayer().sendMessage(ChatColor.GOLD + "------------------------------");
            e.getPlayer().sendMessage(ChatColor.RED + "このIPアドレスからの接続は制限されています。");
            e.getPlayer().sendMessage(ChatColor.GOLD + "理由:");
            kickMessage.forEach(e.getPlayer()::sendMessage);
            e.getPlayer().sendMessage(ChatColor.GOLD + "------------------------------");
        }
        CollectionList<String> messages = ICollectionList.asList((List<String>) map.get("messages")).map(s -> ChatColor.translateAlternateColorCodes('&', s));
        e.getPlayer().sendMessage(ChatColor.GOLD + "------------------------------");
        e.getPlayer().sendMessage(ChatColor.RED + "このIPアドレスからの接続は推奨されていません。");
        e.getPlayer().sendMessage(ChatColor.GOLD + "理由:");
        messages.forEach(e.getPlayer()::sendMessage);
        e.getPlayer().sendMessage(ChatColor.GOLD + "------------------------------");
        if (muteAll) {
            e.getPlayer().sendMessage("");
            e.getPlayer().sendMessage("");
            e.getPlayer().sendMessage(ChatColor.YELLOW + "注意: 現在サーバー管理者によってサーバー全体のチャットが制限されています。");
            e.getPlayer().sendMessage("");
            e.getPlayer().sendMessage("");
        }
    }

    @Override
    public void onDisable() {
        config.save();
    }

    public static CollectionList<UUID> getMutedPlayers() {
        return ICollectionList.asList(config.getStringList("muted")).map(UUID::fromString);
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().startsWith("/kick ") && e.getPlayer().hasPermission("minecraft.command.kick")) {
            sendMessage(e, "`" + e.getPlayer().getName() + "`が`" + e.getMessage().split("\\s+")[1] + "`をKickしました");
        }
        if (e.getMessage().startsWith("/pardon ") && e.getPlayer().hasPermission("minecraft.command.pardon")) {
            sendMessage(e, "`" + e.getPlayer().getName() + "`が`" + e.getMessage().split("\\s+")[1] + "`のBANを解除しました");
        }
        if (e.getMessage().startsWith("/ban ") && e.getPlayer().hasPermission("minecraft.command.ban")) {
            sendMessage(e, "`" + e.getPlayer().getName() + "`が`" + e.getMessage().split("\\s+")[1] + "`をBANしました");
        }
        if (e.getMessage().startsWith("/ban-ip ") && e.getPlayer().hasPermission("minecraft.command.ban-ip")) {
            sendMessage(e, "`" + e.getPlayer().getName() + "`が`" + e.getMessage().split("\\s+")[1] + "`をIP BANしました");
        }
        Bukkit.getOnlinePlayers().stream().filter(Player::isOp).forEach(player -> {
            if (commandLog.contains(player.getUniqueId())) player.sendMessage(ChatColor.GRAY + "[CMD] " + e.getPlayer().getName() + " sent command: " + e.getMessage());
        });
        if (e.getPlayer().isOp()) return;
        if (e.getMessage().startsWith("/tell ") || e.getMessage().startsWith("/w ") || e.getMessage().startsWith("/msg ")) {
            String p = e.getMessage().split(" ")[1];
            Player player = Bukkit.getPlayer(p);
            if (player == null) return;
            if (IgnoreCommand.isPlayerIgnored(player.getUniqueId(), e.getPlayer().getUniqueId()) || getMutedPlayers().contains(e.getPlayer().getUniqueId())) {
                e.getPlayer().sendMessage(ChatColor.RED + "このプレイヤーにプライベートメッセージを送信することはできません。");
                e.setCancelled(true);
            }
        } else if (e.getMessage().startsWith("/me ")) {
            if (HackReport.muteAll || getMutedPlayers().contains(e.getPlayer().getUniqueId())) {
                e.getPlayer().sendMessage(ChatColor.RED + "このコマンドを使用することはできません。");
                e.setCancelled(true);
            }
        }
    }

    public static void sendMessage(PlayerCommandPreprocessEvent e, String message) {
        String[] args = e.getMessage().split("\\s+");
        Webhook webhook = Utils.getWebhook();
        if (webhook == null) return;
        CollectionList<String> list = new CollectionList<>(args);
        list.shift();
        list.shift();
        new Thread(() -> {
            webhook.addEmbed(
                    new Webhook.EmbedObject()
                            .setTitle(message)
                            .setColor(Color.RED)
                            .setDescription("理由: " + list.join(" "))
            );
            try {
                webhook.execute();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player killer = e.getEntity().getKiller();
        if (killer != null) {
            getPlayerInfo(killer.getName(), killer.getUniqueId()).increaseKills();
        }
        getPlayerInfo(e.getEntity().getName(), e.getEntity().getUniqueId()).increaseDeaths();
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
        if (e.getPlayer().isOp() && opChat.contains(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
            OpChatCommand.Do(e.getPlayer().getName(), e.getMessage());
            return;
        }
        if (e.getPlayer().isOp()) return;
        if (HackReport.muteAll || getMutedPlayers().contains(e.getPlayer().getUniqueId())) {
            e.getRecipients().clear();
            return;
        }
        e.getRecipients().removeIf((player -> IgnoreCommand.isPlayerIgnored(player.getUniqueId(), e.getPlayer().getUniqueId())));
    }
}
