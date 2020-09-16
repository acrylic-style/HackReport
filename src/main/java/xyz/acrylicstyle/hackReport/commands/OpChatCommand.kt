package xyz.acrylicstyle.hackReport.commands

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import util.ICollectionList
import xyz.acrylicstyle.hackReport.HackReport
import xyz.acrylicstyle.tomeito_api.command.PlayerCommandExecutor

class OpChatCommand : PlayerCommandExecutor() {
    override fun onCommand(player: Player, args: Array<String>) {
        if (args.size != 0) {
            Do(player.name, ICollectionList.asList(args).join(" "))
            return
        }
        if (HackReport.opChat.contains(player.uniqueId)) {
            HackReport.opChat.remove(player.uniqueId)
            player.sendMessage(PREFIX + "OPChatをオフにしました。")
        } else {
            if (HackReport.modChat.contains(player.uniqueId)) {
                player.sendMessage(ChatColor.RED.toString() + "> OPChatとModChatは同時に使用できません。")
                return
            }
            HackReport.opChat.add(player.uniqueId)
            player.sendMessage(PREFIX + "OPChatをオンにしました。")
        }
    }

    companion object {
        val PREFIX = ChatColor.YELLOW.toString() + "OPChat " + ChatColor.AQUA + ">> " + ChatColor.YELLOW
        fun Do(name: String, message: String?) {
            Bukkit.getOnlinePlayers()
                .stream()
                .filter { obj: Player -> obj.isOp }
                .forEach { player: Player ->
                    player.sendMessage(PREFIX
                        + ChatColor.GOLD + name
                        + ChatColor.WHITE + ": "
                        + ChatColor.YELLOW + ChatColor.translateAlternateColorCodes('&', message))
                }
        }
    }
}