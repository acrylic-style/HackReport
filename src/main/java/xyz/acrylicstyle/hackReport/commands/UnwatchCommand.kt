package xyz.acrylicstyle.hackReport.commands

import org.bukkit.ChatColor
import org.bukkit.entity.Player
import xyz.acrylicstyle.hackReport.HackReport.Companion.stopWatching
import xyz.acrylicstyle.joinChecker.utils.Utils
import xyz.acrylicstyle.tomeito_api.TomeitoAPI
import xyz.acrylicstyle.tomeito_api.command.PlayerOpCommandExecutor

class UnwatchCommand : PlayerOpCommandExecutor() {
    override fun onCommand(player: Player, args: Array<String>) {
        Thread {
            if (Utils.modCheck(player)) return@Thread
            TomeitoAPI.run {
                val target = stopWatching(player)
                if (target == null) {
                    player.sendMessage(ChatColor.RED.toString() + "現在監視している人はいません。")
                    return@run
                }
                if (player.spectatorTarget == target) player.spectatorTarget = null
                player.sendMessage(ChatColor.GOLD.toString() + target.name + ChatColor.GREEN + "の監視をやめました。")
            }
        }.start()
    }
}
