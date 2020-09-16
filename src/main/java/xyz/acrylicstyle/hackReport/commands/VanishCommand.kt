package xyz.acrylicstyle.hackReport.commands

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import xyz.acrylicstyle.hackReport.HackReport
import xyz.acrylicstyle.tomeito_api.command.PlayerCommandExecutor

class VanishCommand : PlayerCommandExecutor() {
    override fun onCommand(player: Player, args: Array<String>) {
        if (HackReport.vanishedPlayers.contains(player.uniqueId)) {
            HackReport.vanishedPlayers.remove(player.uniqueId)
            Bukkit.getOnlinePlayers().forEach { p: Player -> p.showPlayer(player) }
            player.sendMessage(ChatColor.GREEN.toString() + "> Vanishを解除しました。")
        } else {
            HackReport.vanishedPlayers.add(player.uniqueId)
            Bukkit.getOnlinePlayers().forEach { p: Player -> p.hidePlayer(player) }
            player.sendMessage(ChatColor.GREEN.toString() + "> Vanishしました。")
        }
    }
}