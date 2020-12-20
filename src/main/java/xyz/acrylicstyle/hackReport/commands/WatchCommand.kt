package xyz.acrylicstyle.hackReport.commands

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.entity.Player
import xyz.acrylicstyle.hackReport.HackReport
import xyz.acrylicstyle.hackReport.HackReport.Companion.startWatching
import xyz.acrylicstyle.joinChecker.utils.Utils
import xyz.acrylicstyle.tomeito_api.TomeitoAPI
import xyz.acrylicstyle.tomeito_api.command.PlayerOpCommandExecutor

class WatchCommand : PlayerOpCommandExecutor() {
    override fun onCommand(player: Player, args: Array<String>) {
        Thread label@ {
            if (Utils.modCheck(player)) return@label
            if (HackReport.config?.getBoolean("allowModWatch", false) != true && !player.isOp) {
                player.sendMessage("${ChatColor.RED}> 権限がありません。")
                return@label
            }
            if (args.isEmpty()) {
                player.sendMessage("${ChatColor.RED}プレイヤーを指定してください。")
                return@label
            }
            TomeitoAPI.run {
                val target = Bukkit.getPlayerExact(args[0])
                if (target == null) {
                    player.sendMessage(ChatColor.RED.toString() + "プレイヤー'" + args[0] + "'が見つかりません。")
                    return@run
                }
                startWatching(player, target)
                player.gameMode = GameMode.SPECTATOR
                player.spectatorTarget = target
                player.sendMessage(ChatColor.GOLD.toString() + target.name + ChatColor.GREEN + "の監視を開始しました。")
            }
        }.start()
    }
}
