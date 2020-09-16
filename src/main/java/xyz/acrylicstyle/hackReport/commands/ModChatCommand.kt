package xyz.acrylicstyle.hackReport.commands

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import util.Collection
import util.ICollectionList
import xyz.acrylicstyle.hackReport.HackReport
import xyz.acrylicstyle.joinChecker.JoinCheckerManager
import xyz.acrylicstyle.joinChecker.utils.Utils
import xyz.acrylicstyle.tomeito_api.TomeitoAPI
import xyz.acrylicstyle.tomeito_api.command.PlayerCommandExecutor
import java.util.Objects
import java.util.UUID
import java.util.function.Consumer
import java.util.function.Function

class ModChatCommand : PlayerCommandExecutor() {
    override fun onCommand(player: Player, args: Array<String>) {
        Thread label@ {
            if (Utils.modCheck(player)) return@label
            if (args.isNotEmpty()) {
                Do(player.name, ICollectionList.asList(args).join(" "))
                return@label
            }
            if (HackReport.modChat.contains(player.uniqueId)) {
                HackReport.modChat.remove(player.uniqueId)
                player.sendMessage(PREFIX + "ModChatをオフにしました。")
            } else {
                if (HackReport.opChat.contains(player.uniqueId)) {
                    player.sendMessage("${ChatColor.RED}> OPChatとModChatは同時に使用できません。")
                    return@label
                }
                HackReport.modChat.add(player.uniqueId)
                player.sendMessage(PREFIX + "ModChatをオンにしました。")
            }
        }.start()
    }

    companion object {
        val PREFIX = "${ChatColor.GREEN}ModChat " + ChatColor.LIGHT_PURPLE + ">> " + ChatColor.YELLOW
        fun Do(name: String, message: String?) {
            JoinCheckerManager.moderators
                .toMap()
                .then<Any?> { map: Collection<UUID?, String?> ->
                    map.keysList()
                        .map(Function { id: UUID? -> Bukkit.getPlayer(id) } as Function<UUID?, Player>)
                        .filter { obj: Player? -> Objects.nonNull(obj) }
                        .concat(TomeitoAPI.getOnlineOperators())
                        .unique()
                        .forEach(Consumer { player: Player ->
                            player.sendMessage(PREFIX
                                + ChatColor.GOLD + name
                                + ChatColor.WHITE + ": "
                                + ChatColor.YELLOW + ChatColor.translateAlternateColorCodes('&', message))
                        })
                    null
                }.queue()
        }
    }
}