package xyz.acrylicstyle.hackReport.commands

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import util.ICollection
import util.StringCollection
import xyz.acrylicstyle.api.v1_8_R1.MojangAPI
import xyz.acrylicstyle.hackReport.HackReport
import xyz.acrylicstyle.hackReport.HackReport.Companion.instance
import xyz.acrylicstyle.tomeito_api.TomeitoAPI
import xyz.acrylicstyle.tomeito_api.command.PlayerCommandExecutor
import java.util.HashMap
import java.util.UUID

class IgnoreCommand : PlayerCommandExecutor() {
    override fun onCommand(player: Player, args: Array<String>) {
        object : BukkitRunnable() {
            override fun run() {
                if (args.isEmpty()) {
                    player.sendMessage("${ChatColor.YELLOW} - /ignore [add] <プレイヤー>" + ChatColor.GRAY + "- " + ChatColor.AQUA + "Ignoreリストに追加して、指定したプレイヤーのチャットを非表示にします。")
                    player.sendMessage("${ChatColor.YELLOW} - /ignore remove <プレイヤー>" + ChatColor.GRAY + "- " + ChatColor.AQUA + "Ignoreリストからプレイヤーを削除します。")
                    player.sendMessage("${ChatColor.YELLOW} - /ignore list [ページ]" + ChatColor.GRAY + "- " + ChatColor.AQUA + "Ignoreリストを表示します。")
                    return
                }
                if (args[0].equals("add", ignoreCase = true)) {
                    if (args.size == 1) {
                        player.sendMessage("${ChatColor.YELLOW} - /ignore add <プレイヤー>" + ChatColor.GRAY + "- " + ChatColor.AQUA + "Ignoreリストに追加して、指定したプレイヤーのチャットを非表示にします。")
                        return
                    }
                    val uuid = getUniqueId(args[1], player) ?: return
                    val collection = loadIgnoreListPlayer(player.uniqueId).clone()
                    collection.add(uuid.toString(), args[1])
                    saveIgnoreListPlayer(player.uniqueId, collection)
                    player.sendMessage("${ChatColor.GREEN}Ignoreリストに" + args[1] + "を追加しました。")
                } else if (args[0].equals("remove", ignoreCase = true)) {
                    if (args.size == 1) {
                        player.sendMessage("${ChatColor.YELLOW} - /ignore remove <プレイヤー>" + ChatColor.GRAY + "- " + ChatColor.AQUA + "Ignoreリストからプレイヤーを削除します。")
                        return
                    }
                    val uuid = getUniqueId(args[1], player) ?: return
                    val collection = loadIgnoreListPlayer(player.uniqueId).clone()
                    val removed = collection.remove(uuid.toString())
                    if (removed == null) {
                        player.sendMessage(ChatColor.RED.toString() + args[1] + "はIgnoreリストにいません。")
                        return
                    }
                    saveIgnoreListPlayer(player.uniqueId, collection)
                    player.sendMessage("${ChatColor.GREEN}Ignoreリストから" + args[1] + "を削除しました。")
                } else if (args[0].equals("list", ignoreCase = true)) {
                    player.sendMessage("${ChatColor.GREEN}Ignoreリスト: " + ChatColor.YELLOW + loadIgnoreListPlayer(player.uniqueId).valuesList().join("${ChatColor.GRAY}, ${ChatColor.YELLOW}"))
                } else {
                    TomeitoAPI.run { player.chat("/ignore add ${args[0]}") }
                }
            }
        }.runTaskAsynchronously(instance)
    }

    companion object {
        fun loadIgnoreListPlayer(uuid: UUID): StringCollection<String> {
            val map2 = HackReport.getPlayerConfig(uuid).getConfigSectionValue("ignore", true)
                ?: return StringCollection()
            return StringCollection(ICollection.asCollection(map2).mapValues { _: String?, o: Any -> o as String })
        }

        fun saveIgnoreListPlayer(uuid: UUID, collection: StringCollection<String>?) {
            HackReport.getPlayerConfig(uuid).setThenSave("ignore", HashMap(collection))
        }

        fun isPlayerIgnored(message: String, player: Player, target: Player): Boolean {
            if (IgnoreWordCommand.loadIgnoreListPlayer(player.uniqueId).anyMatch { s -> message.contains(s) }) return true
            if (loadIgnoreListPlayer(player.uniqueId).containsKey(target.uniqueId.toString())) return true
            return false
        }

        fun getUniqueId(p: String?, player: CommandSender): UUID? {
            val uuid: UUID? = try {
                MojangAPI.getUniqueId(p!!)
            } catch (e: RuntimeException) {
                player.sendMessage("${ChatColor.RED}プレイヤーが見つかりません。")
                return null
            }
            if (uuid == null) {
                player.sendMessage("${ChatColor.RED}プレイヤーが見つかりません。")
                return null
            }
            if (player is Player && player.uniqueId == uuid) {
                player.sendMessage("${ChatColor.RED}自分自身に対してそのコマンドを実行することはできません。")
                return null
            }
            if (Bukkit.getOfflinePlayer(uuid).isOp) {
                player.sendMessage("${ChatColor.RED}OPを対象にすることはできません。")
                return null
            }
            return uuid
        }
    }
}