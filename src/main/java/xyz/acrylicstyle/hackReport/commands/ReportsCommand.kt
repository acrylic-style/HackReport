package xyz.acrylicstyle.hackReport.commands

import org.bukkit.entity.Player
import xyz.acrylicstyle.hackReport.gui.ReportListGui
import xyz.acrylicstyle.tomeito_api.command.PlayerCommandExecutor

class ReportsCommand : PlayerCommandExecutor() {
    override fun onCommand(player: Player, args: Array<String>) {
        player.openInventory(ReportListGui().register().prepare(player.uniqueId).inventory)
    }
}