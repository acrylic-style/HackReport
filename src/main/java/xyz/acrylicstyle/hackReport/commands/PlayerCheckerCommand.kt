package xyz.acrylicstyle.hackReport.commands

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.scheduler.BukkitRunnable
import xyz.acrylicstyle.hackReport.HackReport.Companion.instance
import xyz.acrylicstyle.joinChecker.utils.Utils
import xyz.acrylicstyle.tomeito_api.command.PlayerCommandExecutor
import xyz.acrylicstyle.tomeito_api.gui.ClickableItem

class PlayerCheckerCommand : PlayerCommandExecutor() {
    override fun onCommand(player: Player, args: Array<String>) {
        Thread label@ {
            if (Utils.modCheck(player)) return@label
            object : BukkitRunnable() {
                override fun run() {
                    player.inventory.addItem(ITEM)
                    player.sendMessage(ChatColor.YELLOW.toString() + "プレイヤーチェッカーをインベントリに追加しました。")
                }
            }.runTask(instance)
        }.start()
    }

    companion object {
        val ITEM = ClickableItem.of(Material.GOLD_NUGGET, ChatColor.GREEN.toString() + "プレイヤーチェッカー") { }.itemStack
    }
}