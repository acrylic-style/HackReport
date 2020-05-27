package xyz.acrylicstyle.hackReport;

import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import util.Collection;
import util.CollectionList;
import util.ICollectionList;
import xyz.acrylicstyle.hackReport.api.event.GlobalChatEvent;
import xyz.acrylicstyle.hackReport.api.event.TeamChatEvent;
import xyz.acrylicstyle.hackReport.commands.*;
import xyz.acrylicstyle.hackReport.gui.*;
import xyz.acrylicstyle.hackReport.utils.PlayerInfo;
import xyz.acrylicstyle.hackReport.utils.ReportDetails;
import xyz.acrylicstyle.tomeito_api.TomeitoAPI;
import xyz.acrylicstyle.tomeito_api.providers.ConfigProvider;
import xyz.acrylicstyle.tomeito_api.utils.Log;

import java.util.UUID;

public class HackReport extends JavaPlugin implements Listener {
    public static final CollectionList<UUID> opChat = new CollectionList<>();
    public static final CollectionList<UUID> commandLog = new CollectionList<>();
    public static final ReportGui REPORT_GUI = new ReportGui();
    public static final ReportConfirmGui REPORT_CONFIRM_GUI = new ReportConfirmGui();
    public static final Collection<UUID, PlayerInfo> PLAYERS = new Collection<>();
    public static final CollectionList<ReportDetails> REPORTS = new CollectionList<>();
    public static final ReportListGui REPORT_LIST_GUI = new ReportListGui();
    public static final ReportList2Gui REPORT_LIST_2_GUI = new ReportList2Gui();
    public static final PlayerActionGui PLAYER_ACTION_GUI = new PlayerActionGui();
    public static ConfigProvider config = null;

    public static LuckPerms luckPerms = null;

    private static HackReport instance;

    public static HackReport getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;
    }

    public static PlayerInfo getPlayerInfo(String name, UUID uuid) {
        if (!PLAYERS.containsKey(uuid)) PLAYERS.add(uuid, new PlayerInfo(name, uuid));
        return PLAYERS.get(uuid);
    }

    @Override
    public void onEnable() {
        Log.info("Loading configuration");
        config = new ConfigProvider("./plugins/HackReport/config.yml");
        Log.info("Registering commands");
        TomeitoAPI.registerCommand("hackreport", new HackReportCommand());
        TomeitoAPI.registerCommand("player", new PlayerCommand());
        TomeitoAPI.registerCommand("report", new ReportCommand());
        TomeitoAPI.registerCommand("reports", new ReportsCommand());
        TomeitoAPI.registerCommand("ignore", new IgnoreCommand());
        TomeitoAPI.registerCommand("mute", new MuteCommand());
        TomeitoAPI.registerCommand("opchat", new OpChat());
        TomeitoAPI.registerCommand("commandlog", new CommandLogCommand());
        Log.info("Registering events");
        Bukkit.getPluginManager().registerEvents(REPORT_GUI, this);
        Bukkit.getPluginManager().registerEvents(REPORT_CONFIRM_GUI, this);
        Bukkit.getPluginManager().registerEvents(REPORT_LIST_GUI, this);
        Bukkit.getPluginManager().registerEvents(REPORT_LIST_2_GUI, this);
        Bukkit.getPluginManager().registerEvents(PLAYER_ACTION_GUI, this);
        Bukkit.getPluginManager().registerEvents(this, this);
        new BukkitRunnable() {
            @Override
            public void run() {
                RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
                if (provider != null) luckPerms = provider.getProvider();
            }
        }.runTaskLater(this, 1);
        Log.info("Enabled HackReport");
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
        Bukkit.getOnlinePlayers().stream().filter(Player::isOp).forEach(player -> {
            if (commandLog.contains(player.getUniqueId())) player.sendMessage(ChatColor.GRAY + "[CMD] " + e.getPlayer().getName() + " sent command: " + e.getMessage());
        });
        if (e.getMessage().startsWith("/tell ") || e.getMessage().startsWith("/w ") || e.getMessage().startsWith("/msg ")) {
            String p = e.getMessage().split(" ")[1];
            Player player = Bukkit.getPlayer(p);
            if (player == null) return;
            if (IgnoreCommand.isPlayerIgnored(player.getUniqueId(), e.getPlayer().getUniqueId()) || getMutedPlayers().contains(e.getPlayer().getUniqueId())) {
                e.getPlayer().sendMessage(ChatColor.RED + "このプレイヤーにプライベートメッセージを送信することはできません。");
                e.setCancelled(true);
            }
        } else if (e.getMessage().startsWith("/me")) {
            if (getMutedPlayers().contains(e.getPlayer().getUniqueId())) {
                e.getPlayer().sendMessage(ChatColor.RED + "このコマンドを使用することはできません。");
                e.setCancelled(true);
            }
        }
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
            OpChat.Do(e.getPlayer().getName(), e.getMessage());
            return;
        }
        if (getMutedPlayers().contains(e.getPlayer().getUniqueId())) {
            e.getRecipients().clear();
            return;
        }
        e.getRecipients().removeIf((player -> IgnoreCommand.isPlayerIgnored(player.getUniqueId(), e.getPlayer().getUniqueId())));
    }

    @EventHandler
    public void onTeamChat(TeamChatEvent e) {
        if (getMutedPlayers().contains(e.getPlayer().getUniqueId())) {
            e.getRecipients().clear();
            return;
        }
        e.getRecipients().removeIf((player -> IgnoreCommand.isPlayerIgnored(player.getUniqueId(), e.getPlayer().getUniqueId())));
    }

    @EventHandler
    public void onGlobalChat(GlobalChatEvent e) {
        if (getMutedPlayers().contains(e.getPlayer().getUniqueId())) {
            e.getRecipients().clear();
            return;
        }
        e.getRecipients().removeIf((player -> IgnoreCommand.isPlayerIgnored(player.getUniqueId(), e.getPlayer().getUniqueId())));
    }
}
