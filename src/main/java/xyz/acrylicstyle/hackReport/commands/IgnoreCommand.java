package xyz.acrylicstyle.hackReport.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import util.DiscordWebhook;
import util.ICollection;
import util.StringCollection;
import xyz.acrylicstyle.api.MojangAPI;
import xyz.acrylicstyle.hackReport.HackReport;
import xyz.acrylicstyle.hackReport.utils.Utils;
import xyz.acrylicstyle.tomeito_api.command.PlayerCommandExecutor;
import xyz.acrylicstyle.tomeito_api.providers.ConfigProvider;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class IgnoreCommand extends PlayerCommandExecutor {
    @Override
    public void onCommand(Player player, String[] args) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (args.length == 0) {
                    player.sendMessage(ChatColor.YELLOW + " - /ignore add <プレイヤー>" + ChatColor.GRAY + "- " + ChatColor.AQUA + "Ignoreリストに追加して、指定したプレイヤーのチャットを非表示にします。");
                    player.sendMessage(ChatColor.YELLOW + " - /ignore remove <プレイヤー>" + ChatColor.GRAY + "- " + ChatColor.AQUA + "Ignoreリストからプレイヤーを削除します。");
                    player.sendMessage(ChatColor.YELLOW + " - /ignore list [ページ]" + ChatColor.GRAY + "- " + ChatColor.AQUA + "Ignoreリストを表示します。");
                    return;
                }
                if (args[0].equalsIgnoreCase("add")) {
                    if (args.length == 1) {
                        player.sendMessage(ChatColor.YELLOW + " - /ignore add <プレイヤー>" + ChatColor.GRAY + "- " + ChatColor.AQUA + "Ignoreリストに追加して、指定したプレイヤーのチャットを非表示にします。");
                        return;
                    }
                    UUID uuid = getUniqueId(args[1], player);
                    if (uuid == null) return;
                    StringCollection<String> collection = loadIgnoreListPlayer(player.getUniqueId()).clone();
                    collection.add(uuid.toString(), args[1]);
                    saveIgnoreListPlayer(player.getUniqueId(), collection);
                    player.sendMessage(ChatColor.GREEN + "Ignoreリストに" + args[1] + "を追加しました。");
                    DiscordWebhook webhook = Utils.getWebhook();
                    if (webhook == null) return;
                    new Thread(() -> {
                        webhook.addEmbed(
                                new DiscordWebhook.EmbedObject()
                                        .setTitle(player.getName() + "が" + args[1] + "をIgnoreリストに追加しました。")
                                        .setColor(Color.YELLOW)
                        );
                        try {
                            webhook.execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                } else if (args[0].equalsIgnoreCase("remove")) {
                    if (args.length == 1) {
                        player.sendMessage(ChatColor.YELLOW + " - /ignore remove <プレイヤー>" + ChatColor.GRAY + "- " + ChatColor.AQUA + "Ignoreリストからプレイヤーを削除します。");
                        return;
                    }
                    UUID uuid = getUniqueId(args[1], player);
                    if (uuid == null) return;
                    StringCollection<String> collection = loadIgnoreListPlayer(player.getUniqueId()).clone();
                    String removed = collection.remove(uuid.toString());
                    if (removed == null) {
                        player.sendMessage(ChatColor.RED + args[1] + "はIgnoreリストにいません。");
                        return;
                    }
                    saveIgnoreListPlayer(player.getUniqueId(), collection);
                    player.sendMessage(ChatColor.GREEN + "Ignoreリストから" + args[1] + "を削除しました。");
                    DiscordWebhook webhook = Utils.getWebhook();
                    if (webhook == null) return;
                    new Thread(() -> {
                        webhook.addEmbed(
                                new DiscordWebhook.EmbedObject()
                                        .setTitle(player.getName() + "が" + args[1] + "をIgnoreリストから削除しました。")
                                        .setColor(Color.YELLOW)
                        );
                        try {
                            webhook.execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                } else if (args[0].equalsIgnoreCase("list")) {
                    player.sendMessage(ChatColor.GREEN + "Ignoreリスト: " + ChatColor.YELLOW + loadIgnoreListPlayer(player.getUniqueId()).valuesList().join(ChatColor.GRAY + ", " + ChatColor.YELLOW));
                } else {
                    player.sendMessage(ChatColor.YELLOW + " - /ignore add <プレイヤー>" + ChatColor.GRAY + "- " + ChatColor.AQUA + "Ignoreリストに追加して、指定したプレイヤーのチャットを非表示にします。");
                    player.sendMessage(ChatColor.YELLOW + " - /ignore remove <プレイヤー>" + ChatColor.GRAY + "- " + ChatColor.AQUA + "Ignoreリストからプレイヤーを削除します。");
                    player.sendMessage(ChatColor.YELLOW + " - /ignore list [ページ]" + ChatColor.GRAY + "- " + ChatColor.AQUA + "Ignoreリストを表示します。");
                }
            }
        }.runTaskAsynchronously(HackReport.getInstance());
    }

    public static StringCollection<String> loadIgnoreListPlayer(UUID uuid) {
        Map<String, Object> map2 = ConfigProvider.getConfig("./plugins/HackReport/players/" + uuid.toString() + ".yml").getConfigSectionValue("ignore", true);
        if (map2 == null) return new StringCollection<>();
        return new StringCollection<>(ICollection.asCollection(map2).mapValues((s, o) -> (String) o));
    }

    public static void saveIgnoreListPlayer(UUID uuid, StringCollection<String> collection) {
        ConfigProvider.getConfig("./plugins/HackReport/players/" + uuid.toString() + ".yml").setThenSave("ignore", new HashMap<>(collection));
    }

    public static boolean isPlayerIgnored(UUID uuid, UUID target) {
        return loadIgnoreListPlayer(uuid).containsKey(target.toString());
    }

    public static UUID getUniqueId(String p, Player player) {
        UUID uuid;
        try {
            uuid = MojangAPI.getUniqueId(p);
        } catch (RuntimeException e) {
            player.sendMessage(ChatColor.RED + "プレイヤーが見つかりません。");
            return null;
        }
        if (uuid == null) {
            player.sendMessage(ChatColor.RED + "プレイヤーが見つかりません。");
            return null;
        }
        if (player.getUniqueId().equals(uuid)) {
            player.sendMessage(ChatColor.RED + "自分自身に対してそのコマンドを実行することはできません。");
            return null;
        }
        if (Bukkit.getOfflinePlayer(uuid).isOp()) {
            player.sendMessage(ChatColor.RED + "OPを対象にすることはできません。");
            return null;
        }
        return uuid;
    }
}
