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
                player.sendMessage("${ChatColor.YELLOW} - /ignoreword [add] <word> ${ChatColor.GRAY}- ${ChatColor.AQUA}Adds a word into ignoreword list and automatically hide chat messages that contains a word")
                player.sendMessage("${ChatColor.YELLOW} - /ignoreword remove <word> ${ChatColor.GRAY}- ${ChatColor.AQUA}Remove a word from ignoreword list.")
                player.sendMessage("${ChatColor.YELLOW} - /ignoreword list ${ChatColor.GRAY}- ${ChatColor.AQUA}Shows ignoreword list.")
                player.sendMessage("${ChatColor.YELLOW} - /ignoreword clear ${ChatColor.GRAY}- ${ChatColor.AQUA}Clears ignoreword list.")
                return@runAsync
            }
            if (args[0].equals("add", ignoreCase = true)) {
                if (args.size == 1) {
                    player.sendMessage("${ChatColor.YELLOW} - /ignoreword add <word>" + ChatColor.GRAY + "- " + ChatColor.AQUA + "Adds a word into ignoreword list and automatically hides chat messages that contains the word")
                    return@runAsync
                }
                val collection = loadIgnoreListPlayer(player.uniqueId).clone()
                if (collection.size >= HackReport.CAP) {
                    if (collection.size > HackReport.CAP) {
                        saveIgnoreListPlayer(player.uniqueId, collection)
                    }
                    player.sendMessage("${ChatColor.RED}Maximum ignoreword list size reached.")
                    return@runAsync
                }
                collection.add(args[1])
                saveIgnoreListPlayer(player.uniqueId, collection)
                player.sendMessage("${ChatColor.GREEN}Added " + args[1] + " to ignoreword list. /ignoreword remove <word> to remove.")
            } else if (args[0].equals("remove", ignoreCase = true)) {
                if (args.size == 1) {
                    player.sendMessage("${ChatColor.YELLOW} - /ignoreword remove <word> ${ChatColor.GRAY}- ${ChatColor.AQUA}Remove a word from ignoreword list.")
                    return@runAsync
                }
                val collection = loadIgnoreListPlayer(player.uniqueId).clone()
                val removed = collection.remove(args[1])
                if (!removed) {
                    player.sendMessage(ChatColor.RED.toString() + args[1] + " is not in ignoreword list.")
                    return@runAsync
                }
                saveIgnoreListPlayer(player.uniqueId, collection)
                player.sendMessage("${ChatColor.GREEN}Removed " + args[1] + " from ignoreword list.")
            } else if (args[0].equals("list", ignoreCase = true)) {
                player.sendMessage(
                    "${ChatColor.GREEN}Ignoreword list: " + ChatColor.YELLOW + loadIgnoreListPlayer(player.uniqueId).join(
                        "${ChatColor.GRAY}, ${ChatColor.YELLOW}"
                    )
                )
            } else if (args[0] == "clear") {
                val collection = loadIgnoreListPlayer(player.uniqueId).clone()
                collection.clear()
                saveIgnoreListPlayer(player.uniqueId, collection)
                player.sendMessage("${ChatColor.GREEN}Your ignoreword list has been cleared.")
            } else if (false && args[0] == "test") {
                if (true) return@runAsync
                val collection = loadIgnoreListPlayer(player.uniqueId).clone()
                for (i in 0..10000) {
                    collection.add(i.toString())
                }
                saveIgnoreListPlayer(player.uniqueId, collection)
                player.sendMessage("${ChatColor.GREEN}Added 10000 entries. Enjoy the lag.")
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
            HackReport.getPlayerConfig(uuid).setThenSave("ignoredWords", collection?.max(HackReport.CAP))
        }
    }
}