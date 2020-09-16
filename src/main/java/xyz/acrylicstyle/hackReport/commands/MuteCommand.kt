package xyz.acrylicstyle.hackReport.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import util.CollectionList;
import util.ICollectionList;
import xyz.acrylicstyle.hackReport.HackReport;
import xyz.acrylicstyle.hackReport.utils.Webhook;
import xyz.acrylicstyle.joinChecker.utils.Utils;
import xyz.acrylicstyle.tomeito_api.sounds.Sound;

import java.awt.*;
import java.util.UUID;

public class MuteCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender player, Command command, String label, String[] args) {
        new Thread(() -> {
            if (Utils.modCheck(player)) return;
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
                CollectionList<String> a = ICollectionList.asList(args);
                a.shift();
                new CollectionList<Player>(Bukkit.getOnlinePlayers()).filter(Player::isOp).forEach(p -> {
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 1);
                    //p.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GREEN + "が" + ChatColor.RED + args[0] + ChatColor.GREEN + "のミュートを解除しました。");
                });
                Bukkit.broadcastMessage(ChatColor.GREEN + player.getName() + "が" + args[0] + "のミュートを解除しました" + (a.size() == 0 ? "。" : ": " + a.join(" ")));
                Webhook.sendWebhook("`" + player.getName() + "`が`" + args[0] + "`のミュートを解除しました。", "理由: " + a.join(" "), Color.GREEN);
            } else {
                list.add(uuid);
                HackReport.config.setThenSave("muted", list.map(UUID::toString).toList());
                CollectionList<String> a = ICollectionList.asList(args);
                a.shift();
                new CollectionList<Player>(Bukkit.getOnlinePlayers()).filter(Player::isOp).forEach(p -> {
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 1);
                    //p.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GREEN + "が" + ChatColor.RED + args[0] + ChatColor.GREEN + "をミュートしました。");
                });
                Bukkit.broadcastMessage(ChatColor.GREEN + player.getName() + "が" + args[0] + "をミュートしました" + (a.size() == 0 ? "。" : ": " + a.join(" ")));
                Webhook.sendWebhook("`" + player.getName() + "`が`" + args[0] + "`をミュートしました。", "理由: " + a.join(" "), Color.RED);
            }
        }).start();
        return true;
    }
}
