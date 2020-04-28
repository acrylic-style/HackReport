package xyz.acrylicstyle.hackReport.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;
import org.bukkit.scheduler.BukkitRunnable;
import util.CollectionList;
import util.ICollectionList;
import xyz.acrylicstyle.api.MojangAPI;
import xyz.acrylicstyle.hackReport.HackReport;
import xyz.acrylicstyle.hackReport.utils.ReportDetails;
import xyz.acrylicstyle.hackReport.utils.Utils;
import xyz.acrylicstyle.tomeito_api.command.PlayerCommandExecutor;

import java.util.UUID;

public class ReportCommand extends PlayerCommandExecutor {
    @Override
    public void onCommand(Player player, String[] args) {
        if (player.isPermissionSet("-hackreport.report")) {
            player.sendMessage(ChatColor.RED + "コマンドを実行する権限がありません。");
            return;
        }
        if (args.length == 0) {
            player.openInventory(HackReport.REPORT_GUI.prepare(player.getUniqueId()).getInventory());
        } else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    String p = args[0];
                    if (player.getName().equalsIgnoreCase(p)) {
                        player.sendMessage(ChatColor.RED + "自分自身を通報することはできません。");
                        return;
                    }
                    UUID uuid = MojangAPI.getUniqueId(p);
                    if (uuid == null) {
                        player.sendMessage(ChatColor.RED + "プレイヤーが見つかりません。");
                        player.sendMessage(ChatColor.RED + "/report <player> <reason>");
                        return;
                    }
                    if (player.getUniqueId().equals(uuid)) {
                        player.sendMessage(ChatColor.RED + "自分自身を通報することはできません。");
                        return;
                    }
                    if (Bukkit.getOfflinePlayer(uuid).isOp()) {
                        player.sendMessage(ChatColor.RED + "OPを通報することはできません。");
                        return;
                    }
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            CollectionList<String> list = ICollectionList.asList(args);
                            list.shift();
                            HackReport.getPlayerInfo(args[0], uuid).increaseReports();
                            HackReport.REPORTS.add(new ReportDetails(args[0], uuid, list));
                            Utils.getOnlinePlayers().filter(ServerOperator::isOp).forEach(p2 -> {
                                p2.playSound(p2.getLocation(), Utils.BLOCK_NOTE_PLING, 100, 0);
                                p2.sendMessage(ChatColor.GREEN + "通報: " + ChatColor.RED + args[0] + ChatColor.GREEN + " from " + ChatColor.YELLOW + player.getName());
                            });
                        }
                    }.runTask(HackReport.getInstance());
                }
            }.runTaskAsynchronously(HackReport.getInstance());
        }
    }
}
