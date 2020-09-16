package xyz.acrylicstyle.hackReport.commands

import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import xyz.acrylicstyle.hackReport.utils.ConnectionHolder.Companion.muteList
import xyz.acrylicstyle.hackReport.utils.Utils
import xyz.acrylicstyle.shared.BaseMojangAPI

class LookupMuteCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        Thread t@ {
            if (xyz.acrylicstyle.joinChecker.utils.Utils.modCheck(sender)) return@t
            if (args.isEmpty()) {
                sender.sendMessage("${ChatColor.RED}プレイヤーを指定してください。")
                return@t
            }
            val uuid = try { BaseMojangAPI.getUniqueId(args[0]) } catch (e: RuntimeException) {
                sender.sendMessage("${ChatColor.RED}プレイヤーが見つかりません。")
                return@t
            }
            muteList.get(uuid).then {
                if (it == null) {
                    sender.sendMessage("${ChatColor.GREEN}${args[0]}はミュートされていません。")
                    return@then
                }
                val executor = if (it.executor == null) "<不明>" else BaseMojangAPI.getName(it.executor)
                sender.sendMessage("${ChatColor.YELLOW}${ChatColor.BOLD}${ChatColor.STRIKETHROUGH}------------------------------")
                sender.sendMessage("${ChatColor.YELLOW} - UUID: ${ChatColor.AQUA}${it.uuid}")
                sender.sendMessage("${ChatColor.YELLOW} - プレイヤー名: ${ChatColor.GOLD}${it.name}")
                sender.sendMessage("${ChatColor.YELLOW} - 理由: ${ChatColor.RED}${it.reason}")
                sender.sendMessage("${ChatColor.YELLOW} - ミュートした人: ${ChatColor.GOLD}${executor}")
                sender.sendMessage("${ChatColor.YELLOW} - ミュートされた日時: ${ChatColor.LIGHT_PURPLE}${Utils.timestampToDate(it.timestamp)}")
                if (it.expiresAt == -1L) {
                    sender.sendMessage("${ChatColor.YELLOW} - このミュートは永久です。")
                } else {
                    sender.sendMessage("${ChatColor.YELLOW} - ミュートが解除される日時: ${ChatColor.LIGHT_PURPLE}${Utils.timestampToDate(it.expiresAt)} (あと${Utils.timestampToDay(it.expiresAt - System.currentTimeMillis())})")
                }
                sender.sendMessage("${ChatColor.YELLOW}${ChatColor.BOLD}${ChatColor.STRIKETHROUGH}------------------------------")
            }.queue()
        }.start()
        return true
    }
}
