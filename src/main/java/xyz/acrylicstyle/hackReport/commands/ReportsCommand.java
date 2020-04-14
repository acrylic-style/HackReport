package xyz.acrylicstyle.hackReport.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.acrylicstyle.hackReport.HackReport;
import xyz.acrylicstyle.tomeito_core.command.Command;
import xyz.acrylicstyle.tomeito_core.command.PlayerCommandExecutor;

@Command("reports")
public class ReportsCommand extends PlayerCommandExecutor {
    @Override
    public void onCommand(Player player, String[] args) {
        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + "You don't have permission to do this.");
            return;
        }
        player.openInventory(HackReport.REPORT_LIST_GUI.prepare(player.getUniqueId()).getInventory());
    }
}
