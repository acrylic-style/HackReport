package xyz.acrylicstyle.hackReport.commands

import org.bukkit.ChatColor
import org.bukkit.entity.Player
import xyz.acrylicstyle.api.v1_8_R1.MojangAPI
import xyz.acrylicstyle.hackReport.gui.PlayerActionGui
import xyz.acrylicstyle.tomeito_api.command.PlayerCommandExecutor

class PlayerCommand : PlayerCommandExecutor() {
    override fun onCommand(player: Player, args: Array<String>) {
        if (args.isEmpty()) {
            player.sendMessage(ChatColor.RED.toString() + "/player <player>")
            return
        }
        val p = args[0]
        val uuid = MojangAPI.getUniqueId(p)
        if (uuid == null) {
            player.sendMessage(ChatColor.RED.toString() + "プレイヤーが見つかりません。")
            player.sendMessage(ChatColor.RED.toString() + "/player <player>")
            return
        }
        player.openInventory(PlayerActionGui().register().prepare(player, args[0], uuid).inventory)
    }
}