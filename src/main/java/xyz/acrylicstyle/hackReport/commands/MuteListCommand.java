package xyz.acrylicstyle.hackReport.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.acrylicstyle.hackReport.HackReport;
import xyz.acrylicstyle.shared.BaseMojangAPI;
import xyz.acrylicstyle.tomeito_api.command.PlayerCommandExecutor;

import java.util.Objects;

public class MuteListCommand extends PlayerCommandExecutor {
    @Override
    public void onCommand(Player player, String[] strings) {
        new Thread(() -> {
            String mutedPpls = HackReport.getMutedPlayers().filter(Objects::nonNull).map(BaseMojangAPI::getName).join(ChatColor.YELLOW + ", " + ChatColor.GREEN);
            player.sendMessage(ChatColor.GREEN + "ミュートされているプレイヤー:");
            player.sendMessage(ChatColor.GREEN + mutedPpls);
        }).start();
    }
}
