package xyz.acrylicstyle.hackReport.commands;

import org.bukkit.entity.Player;
import xyz.acrylicstyle.hackReport.HackReport;
import xyz.acrylicstyle.tomeito_core.command.Command;
import xyz.acrylicstyle.tomeito_core.command.PlayerOpCommandExecutor;

@Command("reports")
public class ReportsCommand extends PlayerOpCommandExecutor {
    @Override
    public void onCommand(Player player, String[] args) {
        player.openInventory(HackReport.REPORT_LIST_GUI.prepare(player.getUniqueId()).getInventory());
    }
}
