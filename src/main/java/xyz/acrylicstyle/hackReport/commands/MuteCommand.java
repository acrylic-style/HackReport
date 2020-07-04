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
import xyz.acrylicstyle.hackReport.utils.Utils;
import xyz.acrylicstyle.hackReport.utils.Webhook;
import xyz.acrylicstyle.tomeito_api.sounds.Sound;

import java.awt.*;
import java.io.IOException;
import java.util.UUID;

public class MuteCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender player, Command command, String label, String[] args) {
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "/mute <Player> " + ChatColor.GRAY + "- " + ChatColor.AQUA + "プレイヤーをミュート/ミュート解除します。");
            return true;
        }
        CollectionList<UUID> list = ICollectionList.asList(HackReport.config.getStringList("muted")).map(UUID::fromString);
        UUID uuid = IgnoreCommand.getUniqueId(args[0], player);
        if (uuid == null) return true;
        if (list.contains(uuid)) {
            list.remove(uuid);
            HackReport.config.setThenSave("muted", list.map(UUID::toString).toList());
            CollectionList<String> a = ICollectionList.asList(args);
            a.shift();
            new CollectionList<Player>(Bukkit.getOnlinePlayers()).filter(Player::isOp).forEach(p -> {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 1);
                //p.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GREEN + "が" + ChatColor.RED + args[0] + ChatColor.GREEN + "のミュートを解除しました。");
                Webhook webhook = Utils.getWebhook();
                if (webhook == null) return;
                new Thread(() -> {
                    webhook.addEmbed(
                            new Webhook.EmbedObject()
                                    .setTitle("`" + player.getName() + "`が`" + args[0] + "`のミュートを解除しました。")
                                    .setColor(Color.GREEN)
                                    .setDescription("理由: " + a.join(" "))
                    );
                    try {
                        webhook.execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            });
            Bukkit.broadcastMessage(ChatColor.GREEN + player.getName() + "が" + args[0] + "のミュートを解除しました: " + a.join(" "));
        } else {
            list.add(uuid);
            HackReport.config.setThenSave("muted", list.map(UUID::toString).toList());
            CollectionList<String> a = ICollectionList.asList(args);
            a.shift();
            new CollectionList<Player>(Bukkit.getOnlinePlayers()).filter(Player::isOp).forEach(p -> {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 1);
                //p.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GREEN + "が" + ChatColor.RED + args[0] + ChatColor.GREEN + "をミュートしました。");
                Webhook webhook = Utils.getWebhook();
                if (webhook == null) return;
                new Thread(() -> {
                    webhook.addEmbed(
                            new Webhook.EmbedObject()
                                    .setTitle("`" + player.getName() + "`が`" + args[0] + "`をミュートしました。")
                                    .setColor(Color.RED)
                                    .setDescription("理由: " + a.join(" "))
                    );
                    try {
                        webhook.execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            });
            Bukkit.broadcastMessage(ChatColor.GREEN + player.getName() + "が" + args[0] + "をミュートしました: " + a.join(" "));
        }
        return true;
    }
}
