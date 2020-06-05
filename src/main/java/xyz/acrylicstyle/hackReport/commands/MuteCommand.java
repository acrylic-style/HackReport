package xyz.acrylicstyle.hackReport.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import util.CollectionList;
import util.ICollectionList;
import xyz.acrylicstyle.hackReport.HackReport;
import xyz.acrylicstyle.tomeito_api.command.PlayerCommandExecutor;
import xyz.acrylicstyle.tomeito_api.sounds.Sound;

import java.util.UUID;

public class MuteCommand extends PlayerCommandExecutor {
    @Override
    public void onCommand(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "/mute <Player> " + ChatColor.GRAY + "- " + ChatColor.AQUA + "プレイヤーをミュート/ミュート解除します。");
            return;
        }
        CollectionList<UUID> list = ICollectionList.asList(HackReport.config.getStringList("muted")).map(UUID::fromString);
        UUID uuid = IgnoreCommand.getUniqueId(args[0], player);
        if (uuid == null) return;
        if (list.contains(uuid)) {
            list.remove(uuid);
            HackReport.config.setThenSave("muted", list.map(UUID::toString).toList());
            new CollectionList<Player>(Bukkit.getOnlinePlayers()).filter(Player::isOp).forEach(p -> {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 1);
                p.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GREEN + "が" + ChatColor.RED + args[0] + ChatColor.GREEN + "のミュートを解除しました。");
            });
        } else {
            list.add(uuid);
            HackReport.config.setThenSave("muted", list.map(UUID::toString).toList());
            new CollectionList<Player>(Bukkit.getOnlinePlayers()).filter(Player::isOp).forEach(p -> {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 1);
                p.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GREEN + "が" + ChatColor.RED + args[0] + ChatColor.GREEN + "をミュートしました。");
            });
        }
    }
}
