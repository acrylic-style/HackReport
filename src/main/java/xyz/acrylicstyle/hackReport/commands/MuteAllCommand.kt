package xyz.acrylicstyle.hackReport.commands

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import util.CollectionList
import xyz.acrylicstyle.hackReport.HackReport
import xyz.acrylicstyle.tomeito_api.command.PlayerCommandExecutor
import xyz.acrylicstyle.tomeito_api.sounds.Sound
import java.util.function.Consumer

class MuteAllCommand : PlayerCommandExecutor() {
    override fun onCommand(player: Player, args: Array<String>) {
        if (HackReport.muteAll) {
            HackReport.muteAll = false
            CollectionList(Bukkit.getOnlinePlayers()).forEach(Consumer { p: Player ->
                p.playSound(p.location, Sound.BLOCK_NOTE_PLING, 100f, 1f)
                p.sendMessage("${ChatColor.YELLOW}${player.name}${ChatColor.GREEN}がこのチャットのミュートを${ChatColor.LIGHT_PURPLE}解除${ChatColor.GREEN}しました。")
            })
        } else {
            HackReport.muteAll = true
            CollectionList(Bukkit.getOnlinePlayers()).forEach(Consumer { p: Player ->
                p.playSound(p.location, Sound.BLOCK_NOTE_PLING, 100f, 1f)
                p.sendMessage("${ChatColor.YELLOW}${player.name}${ChatColor.GREEN}がこのチャットを${ChatColor.RED}ミュート${ChatColor.GREEN}しました。")
            })
        }
    }
}