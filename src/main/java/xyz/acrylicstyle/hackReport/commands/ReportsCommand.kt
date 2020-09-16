package xyz.acrylicstyle.hackReport.commands;

import org.bukkit.entity.Player;
import xyz.acrylicstyle.hackReport.gui.ReportListGui;
import xyz.acrylicstyle.tomeito_api.command.PlayerCommandExecutor;

public class ReportsCommand extends PlayerCommandExecutor {
    @Override
    public void onCommand(Player player, String[] args) {
        player.openInventory(new ReportListGui().register().prepare(player.getUniqueId()).getInventory());
    }
}
