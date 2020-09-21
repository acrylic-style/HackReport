package xyz.acrylicstyle.hackReport.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import xyz.acrylicstyle.hackReport.HackReport
import xyz.acrylicstyle.joinChecker.utils.Utils

class AMuteCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        Thread t@ {
            if (Utils.modCheck(sender)) return@t
            Bukkit.getScheduler().runTask(HackReport.instance) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mute " + args.joinToString(" "))
            }
        }.start()
        return true
    }
}
