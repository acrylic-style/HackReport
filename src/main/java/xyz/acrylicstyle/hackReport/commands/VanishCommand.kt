package xyz.acrylicstyle.hackReport.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.acrylicstyle.hackReport.HackReport;
import xyz.acrylicstyle.tomeito_api.command.PlayerCommandExecutor;

public class VanishCommand extends PlayerCommandExecutor {
    @Override
    public void onCommand(Player player, String[] args) {
        if (HackReport.vanishedPlayers.contains(player.getUniqueId())) {
            HackReport.vanishedPlayers.remove(player.getUniqueId());
            Bukkit.getOnlinePlayers().forEach(p -> p.showPlayer(player));
            player.sendMessage(ChatColor.GREEN + "> Vanishを解除しました。");
        } else {
            HackReport.vanishedPlayers.add(player.getUniqueId());
            Bukkit.getOnlinePlayers().forEach(p -> p.hidePlayer(player));
            player.sendMessage(ChatColor.GREEN + "> Vanishしました。");
        }
    }
}
