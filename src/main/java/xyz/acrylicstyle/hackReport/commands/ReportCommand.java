package xyz.acrylicstyle.hackReport.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;
import util.CollectionList;
import util.ICollectionList;
import xyz.acrylicstyle.api.MojangAPI;
import xyz.acrylicstyle.hackReport.HackReport;
import xyz.acrylicstyle.hackReport.utils.ReportDetails;
import xyz.acrylicstyle.hackReport.utils.Utils;
import xyz.acrylicstyle.tomeito_core.command.Command;
import xyz.acrylicstyle.tomeito_core.command.PlayerCommandExecutor;

import java.util.UUID;

@Command("report")
public class ReportCommand extends PlayerCommandExecutor {
    @Override
    public void onCommand(Player player, String[] args) {
        if (args.length == 0) {
            player.openInventory(HackReport.REPORT_GUI.prepare(player.getUniqueId()).getInventory());
        } else {
            String p = args[0];
            UUID uuid = MojangAPI.getUniqueId(p);
            if (uuid == null) {
                player.sendMessage(ChatColor.RED + "プレイヤーが見つかりません。");
                player.sendMessage(ChatColor.RED + "/report <player> <reason>");
                return;
            }
            CollectionList<String> list = ICollectionList.asList(args);
            list.shift();
            HackReport.getPlayerInfo(args[0], uuid).increaseReports();
            HackReport.REPORTS.add(new ReportDetails(args[0], uuid, list));
            Utils.getOnlinePlayers().filter(ServerOperator::isOp).forEach(p2 -> {
                p2.playSound(p2.getLocation(), Utils.BLOCK_NOTE_PLING, 100, 0);
                p2.sendMessage(ChatColor.GREEN + "通報: " + ChatColor.RED + args[0] + ChatColor.GREEN + " from " + ChatColor.YELLOW + player.getName());
            });
        }
    }
}
