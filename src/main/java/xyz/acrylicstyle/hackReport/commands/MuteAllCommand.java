package xyz.acrylicstyle.hackReport.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import util.CollectionList;
import xyz.acrylicstyle.hackReport.HackReport;
import xyz.acrylicstyle.tomeito_api.command.PlayerCommandExecutor;
import xyz.acrylicstyle.tomeito_api.sounds.Sound;

public class MuteAllCommand extends PlayerCommandExecutor {
    @Override
    public void onCommand(Player player, String[] args) {
        if (HackReport.muteAll) {
            HackReport.muteAll = false;
            new CollectionList<Player>(Bukkit.getOnlinePlayers()).forEach(p -> {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 1);
                p.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GREEN + "がこのチャットのミュートを" + ChatColor.LIGHT_PURPLE + "解除" + ChatColor.GREEN + "しました。");
            });
        } else {
            HackReport.muteAll = true;
            new CollectionList<Player>(Bukkit.getOnlinePlayers()).forEach(p -> {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 1);
                p.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GREEN + "がこのチャットを" + ChatColor.RED + "ミュート" + ChatColor.GREEN + "しました。");
            });
        }
    }
}
