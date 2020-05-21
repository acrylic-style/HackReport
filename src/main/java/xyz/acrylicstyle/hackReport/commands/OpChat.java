package xyz.acrylicstyle.hackReport.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import util.ICollectionList;
import xyz.acrylicstyle.hackReport.HackReport;
import xyz.acrylicstyle.tomeito_api.command.PlayerOpCommandExecutor;

public class OpChat extends PlayerOpCommandExecutor {
    public static final String PREFIX = ChatColor.YELLOW + "OPChat " + ChatColor.AQUA + ">> " + ChatColor.YELLOW;

    @Override
    public void onCommand(Player player, String[] args) {
        if (args.length != 0) {
            Do(player.getName(), ICollectionList.asList(args).join(" "));
            return;
        }
        if (HackReport.opChat.contains(player.getUniqueId())) {
            HackReport.opChat.remove(player.getUniqueId());
            player.sendMessage(PREFIX + "OPChatをオフにしました。");
        } else {
            HackReport.opChat.add(player.getUniqueId());
            player.sendMessage(PREFIX + "OPChatをオンにしました。");
        }
    }

    public static void Do(String name, String message) {
        Bukkit.getOnlinePlayers()
                .stream()
                .filter(Player::isOp)
                .forEach(player -> player.sendMessage(OpChat.PREFIX
                        + ChatColor.GOLD + name
                        + ChatColor.WHITE + ": "
                        + ChatColor.YELLOW + ChatColor.translateAlternateColorCodes('&', message)));
    }
}
