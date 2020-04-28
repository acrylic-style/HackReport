package xyz.acrylicstyle.hackReport.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import util.Collection;
import util.ICollection;
import xyz.acrylicstyle.api.MojangAPI;
import xyz.acrylicstyle.hackReport.HackReport;
import xyz.acrylicstyle.tomeito_api.command.PlayerCommandExecutor;
import xyz.acrylicstyle.tomeito_api.utils.TypeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class IgnoreCommand extends PlayerCommandExecutor {
    @SuppressWarnings("unchecked")
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
                    Collection<UUID, String> collection = loadIgnoreListPlayer(player.getUniqueId());
                    collection.add(uuid, args[1]);
                    saveIgnoreListPlayer(player.getUniqueId(), collection);
                    player.sendMessage(ChatColor.GREEN + "Ignoreリストに" + args[1] + "を追加しました。");
                } else if (args[0].equalsIgnoreCase("remove")) {
                    if (args.length == 1) {
                        player.sendMessage(ChatColor.YELLOW + " - /ignore remove <プレイヤー>" + ChatColor.GRAY + "- " + ChatColor.AQUA + "Ignoreリストからプレイヤーを削除します。");
                        return;
                    }
                    UUID uuid = getUniqueId(args[1], player);
                    if (uuid == null) return;
                    Collection<UUID, String> collection = loadIgnoreListPlayer(player.getUniqueId());
                    collection.remove(uuid);
                    saveIgnoreListPlayer(uuid, collection);
                    player.sendMessage(ChatColor.GREEN + "Ignoreリストから" + args[1] + "を削除しました。");
                } else if (args[0].equalsIgnoreCase("list")) {
                    int page = 1;
                    if (args[1] != null) {
                        if (!TypeUtil.isInt(args[1])) {
                            player.sendMessage(ChatColor.LIGHT_PURPLE + "/ignore list [page]");
                            return;
                        }
                        page = Integer.parseInt(args[1]);
                        if (page < 1) {
                            player.sendMessage(ChatColor.RED + "ページは1より小さくすることはできません。");
                            return;
                        }
                    }
                    int finalPage = page;
                    player.sendMessage(ChatColor.GOLD + String.format("----- Ignored players [Page %d] -----", finalPage));
                    loadIgnoreListPlayer(player.getUniqueId()).forEach((uuid, name, i, map) -> {
                        if (i > 12*(finalPage-1) && i < 12*finalPage) player.sendMessage(ChatColor.GRAY + " - " + ChatColor.GREEN + name);
                    });
                } else {
                    player.sendMessage(ChatColor.YELLOW + " - /ignore add <プレイヤー>" + ChatColor.GRAY + "- " + ChatColor.AQUA + "Ignoreリストに追加して、指定したプレイヤーのチャットを非表示にします。");
                    player.sendMessage(ChatColor.YELLOW + " - /ignore remove <プレイヤー>" + ChatColor.GRAY + "- " + ChatColor.AQUA + "Ignoreリストからプレイヤーを削除します。");
                    player.sendMessage(ChatColor.YELLOW + " - /ignore list [ページ]" + ChatColor.GRAY + "- " + ChatColor.AQUA + "Ignoreリストを表示します。");
                }
            }
        }.runTaskAsynchronously(HackReport.getInstance());
        Map<String, Object> map = HackReport.config.getConfigSectionValue("ignore", true);
        Collection<String, Object> rawCollection = map == null ? new Collection<>() : ICollection.asCollection(map);
        Collection<UUID, List<String>> collection = rawCollection.map((s, o) -> UUID.fromString(s), (s, o) -> (List<String>) o);
        List<String> list = collection.getOrDefault(player.getUniqueId(), new ArrayList<>());
    }

    private static Collection<UUID, String> loadIgnoreListPlayer(UUID uuid) {
        Map<String, Object> map2 = HackReport.config.getConfigSectionValue("ignore." + uuid.toString(), true);
        if (map2 == null) return new Collection<>();
        return ICollection.asCollection(map2).map((s, o) -> UUID.fromString(s), (s, o) -> (String) o);
    }

    private static void saveIgnoreListPlayer(UUID uuid, Collection<UUID, String> collection) {
        HackReport.config.set("ignore." + uuid.toString(), collection.mapKeys((u, s) -> u.toString()));
    }

    private static Collection<UUID, Collection<UUID, String>> loadIgnoreList() {
        Collection<UUID, Collection<UUID, String>> collection = new Collection<>();
        Map<String, Object> map = HackReport.config.getConfigSectionValue("ignore", true);
        map.keySet().forEach(s -> collection.add(UUID.fromString(s), loadIgnoreListPlayer(UUID.fromString(s))));
        return collection;
    }

    private static void saveIgnoreList(Collection<UUID, Collection<UUID, String>> collection) {
        collection.forEach(IgnoreCommand::saveIgnoreListPlayer);
    }

    public static boolean isPlayerIgnored(UUID uuid, UUID target) {
        return loadIgnoreListPlayer(uuid).containsKey(target);
    }

    private UUID getUniqueId(String p, Player player) {
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
            player.sendMessage(ChatColor.RED + "自分自身をIgnoreすることはできません。");
            return null;
        }
        if (Bukkit.getOfflinePlayer(uuid).isOp()) {
            player.sendMessage(ChatColor.RED + "OPをIgnoreすることはできません。");
            return null;
        }
        return uuid;
    }
}
