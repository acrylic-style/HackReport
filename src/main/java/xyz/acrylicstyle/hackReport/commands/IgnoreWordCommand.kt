package xyz.acrylicstyle.hackReport.commands

import org.bukkit.ChatColor
import org.bukkit.entity.Player
import util.CollectionSet
import xyz.acrylicstyle.hackReport.HackReport
import xyz.acrylicstyle.tomeito_api.TomeitoAPI
import xyz.acrylicstyle.tomeito_api.command.PlayerCommandExecutor
import java.util.UUID

class IgnoreWordCommand : PlayerCommandExecutor() {
    override fun onCommand(player: Player, args: Array<String>) {
        TomeitoAPI.runAsync {
            if (args.isEmpty()) {
                player.sendMessage("${ChatColor.YELLOW} - /ignoreword [add] <プレイヤー>" + ChatColor.GRAY + "- " + ChatColor.AQUA + "Ignoreリストに単語を追加して、指定した単語が含まれるチャットを非表示にします。")
                player.sendMessage("${ChatColor.YELLOW} - /ignoreword remove <プレイヤー>" + ChatColor.GRAY + "- " + ChatColor.AQUA + "Ignoreリストから単語を削除します。")
                player.sendMessage("${ChatColor.YELLOW} - /ignoreword list [ページ]" + ChatColor.GRAY + "- " + ChatColor.AQUA + "単語のIgnoreリストを表示します。")
                return@runAsync
            }
            if (args[0].equals("add", ignoreCase = true)) {
                if (args.size == 1) {
                    player.sendMessage("${ChatColor.YELLOW} - /ignoreword add <プレイヤー>" + ChatColor.GRAY + "- " + ChatColor.AQUA + "Ignoreリストに単語を追加して、指定した単語が含まれるチャットを非表示にします。")
                    return@runAsync
                }
                val collection = loadIgnoreListPlayer(player.uniqueId).clone()
                collection.add(args[1])
                saveIgnoreListPlayer(player.uniqueId, collection)
                player.sendMessage("${ChatColor.GREEN}Ignore単語リストに" + args[1] + "を追加しました。")
            } else if (args[0].equals("remove", ignoreCase = true)) {
                if (args.size == 1) {
                    player.sendMessage("${ChatColor.YELLOW} - /ignoreword remove <プレイヤー>" + ChatColor.GRAY + "- " + ChatColor.AQUA + "Ignoreリストから単語を削除します。")
                    return@runAsync
                }
                val collection = loadIgnoreListPlayer(player.uniqueId).clone()
                val removed = collection.remove(args[1])
                if (!removed) {
                    player.sendMessage(ChatColor.RED.toString() + args[1] + "はIgnoreリストにいません。")
                    return@runAsync
                }
                saveIgnoreListPlayer(player.uniqueId, collection)
                player.sendMessage("${ChatColor.GREEN}Ignore単語リストから" + args[1] + "を削除しました。")
            } else if (args[0].equals("list", ignoreCase = true)) {
                player.sendMessage(
                    "${ChatColor.GREEN}Ignore単語リスト: " + ChatColor.YELLOW + loadIgnoreListPlayer(player.uniqueId).join(
                        "${ChatColor.GRAY}, ${ChatColor.YELLOW}"
                    )
                )
            } else {
                TomeitoAPI.run { player.chat("/ignoreword add ${args[0]}") }
            }
        }
    }

    companion object {
        fun loadIgnoreListPlayer(uuid: UUID): CollectionSet<String> {
            return CollectionSet(HackReport.getPlayerConfig(uuid).getStringList("ignoredWords") ?: ArrayList())
        }

        fun saveIgnoreListPlayer(uuid: UUID, collection: CollectionSet<String>?) {
            HackReport.getPlayerConfig(uuid).setThenSave("ignoredWords", collection)
        }
    }
}