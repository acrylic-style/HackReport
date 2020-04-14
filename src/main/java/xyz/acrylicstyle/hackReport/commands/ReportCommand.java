package xyz.acrylicstyle.hackReport.commands;

import org.bukkit.entity.Player;
import xyz.acrylicstyle.hackReport.HackReport;
import xyz.acrylicstyle.tomeito_core.command.Command;
import xyz.acrylicstyle.tomeito_core.command.PlayerCommandExecutor;

@Command("report")
public class ReportCommand extends PlayerCommandExecutor {
    @Override
    public void onCommand(Player player, String[] args) {
        player.openInventory(HackReport.REPORT_GUI.prepare(player.getUniqueId()).getInventory());
    }
}
