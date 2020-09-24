package xyz.acrylicstyle.hackReport.commands

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import util.CollectionList
import util.ICollectionList
import xyz.acrylicstyle.hackReport.struct.Mute
import xyz.acrylicstyle.hackReport.utils.ConnectionHolder.Companion.muteList
import xyz.acrylicstyle.hackReport.utils.Webhook
import xyz.acrylicstyle.joinChecker.utils.Utils
import xyz.acrylicstyle.tomeito_api.TomeitoAPI
import xyz.acrylicstyle.tomeito_api.sounds.Sound
import xyz.acrylicstyle.tomeito_api.utils.TypeUtil
import java.awt.Color
import java.util.function.Consumer

class MuteCommand : CommandExecutor {
    override fun onCommand(player: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        Thread t@ {
            if (Utils.modCheck(player)) return@t
            if (args.isEmpty()) {
                player.sendMessage("${ChatColor.RED}/mute <Player> [<time> <m/h/d>] [reason] ${ChatColor.GRAY}- ${ChatColor.AQUA}プレイヤーをミュート/ミュート解除します。")
                return@t
            }
            mute(player, args, false)
        }.start()
        return true
    }

    companion object {
        const val DAY: Long = 86_400_000
        const val HOUR: Long = 3_600_000
        const val MINUTE: Long =  60_000

        fun mute(player: CommandSender, args: Array<String>, tell: Boolean) {
            val ps = args[0]
            val arg: CollectionList<String>
            run {
                val list = ICollectionList.asList(args) // doing this in block intentionally, to prevent access to "list" from outside of this
                if (args.size > 1) {
                    if (args[1].matches("\\d+[mhd]".toRegex())) {
                        list.shift()
                        list.shift()
                        list.add(0, args[1].replace("\\d+([mhd])".toRegex(), "$1"))
                        list.add(0, args[1].replace("(\\d+)[mhd]".toRegex(), "$1"))
                        list.add(0, ps)
                    }
                }
                arg = list.clone()
            }
            val uuid = IgnoreCommand.getUniqueId(arg.shift(), player) ?: return
            val d7 = System.currentTimeMillis() + 7 * DAY
            var expiresAt = -1L
            if (arg.size >= 3) {
                if (TypeUtil.isInt(arg[0])) {
                    expiresAt = when (arg[1]) {
                        "d" -> System.currentTimeMillis() + arg[0].toLong() * DAY
                        "h" -> System.currentTimeMillis() + arg[0].toLong() * HOUR
                        "m" -> System.currentTimeMillis() + arg[0].toLong() * MINUTE
                        else -> -1
                    }
                    if (expiresAt != -1L) arg.shiftChain().shiftChain()
                }
            }
            if (expiresAt == -1L) expiresAt = d7
            val reason = arg.join(" ")
            val emptyReason = arg.isEmpty()
            if (muteList.contains(uuid, tell)) {
                muteList.remove(uuid, tell)
                TomeitoAPI.getOnlineOperators().forEach(Consumer { p: Player ->
                    p.playSound(p.location, Sound.BLOCK_NOTE_PLING, 100f, 1f)
                    if (tell) p.sendMessage("${ChatColor.GREEN}${player.name}が" + ps + "のTellミュートを解除しました" + if (emptyReason) "。" else ": $reason")
                })
                if (!tell) Bukkit.broadcastMessage("${ChatColor.GREEN}${player.name}が" + ps + "のミュートを解除しました" + if (emptyReason) "。" else ": $reason")
                Webhook.sendWebhook("`${player.name}`が`${ps}`の${if (tell) "Tell" else ""}ミュートを解除しました。", "理由: $reason", Color.GREEN)
            } else {
                if (emptyReason) {
                    player.sendMessage("${ChatColor.RED}理由を指定してください。")
                    return
                }
                muteList.add(
                    Mute(
                        uuid,
                        ps,
                        reason,
                        if (player is Player) player.uniqueId else null,
                        System.currentTimeMillis(),
                        expiresAt,
                    ),
                    tell
                )
                TomeitoAPI.getOnlineOperators().forEach(Consumer { p: Player ->
                    p.playSound(p.location, Sound.BLOCK_NOTE_PLING, 100f, 1f)
                    if (tell) p.sendMessage("${ChatColor.GREEN}${player.name}が${ps}をTellミュートしました: $reason")
                })
                if (!tell) Bukkit.broadcastMessage("${ChatColor.GREEN}${player.name}が${ps}をミュートしました: $reason")
                Webhook.sendWebhook("`${player.name}`が`${ps}`を${if (tell) "Tell" else ""}ミュートしました。", "理由: $reason", Color.RED)
            }
        }
    }
}
