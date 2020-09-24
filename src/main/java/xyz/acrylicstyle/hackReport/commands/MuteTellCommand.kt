package xyz.acrylicstyle.hackReport.commands

import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import xyz.acrylicstyle.joinChecker.utils.Utils

class MuteTellCommand : CommandExecutor {
    override fun onCommand(player: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        Thread t@ {
            if (Utils.modCheck(player)) return@t
            if (args.isEmpty()) {
                player.sendMessage("${ChatColor.RED}/mutetell <Player> [<time> <m/h/d>] [reason] ${ChatColor.GRAY}- ${ChatColor.AQUA}プレイヤーのTellをミュート/ミュート解除します。")
                return@t
            }
            MuteCommand.mute(player, args, true)
        }.start()
        return true
    }
}