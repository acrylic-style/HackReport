package xyz.acrylicstyle.hackReport.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.acrylicstyle.tomeito_core.command.Command;
import xyz.acrylicstyle.tomeito_core.command.PlayerCommandExecutor;

@Command("hackreport")
public class HackReportCommand extends PlayerCommandExecutor {
    @Override
    public void onCommand(Player player, String[] args) {
        player.sendMessage(ChatColor.YELLOW + " - /report [プレイヤー] [理由] " + ChatColor.GRAY + "- " + ChatColor.AQUA + "プレイヤーを通報します。");
        if (player.isOp()) {
            player.sendMessage(ChatColor.YELLOW + " - /reports " + ChatColor.GRAY + "- " + ChatColor.AQUA + "通報一覧を表示します。");
            player.sendMessage(ChatColor.YELLOW + " - /player <プレイヤー> " + ChatColor.GRAY + "- " + ChatColor.AQUA + "プレイヤーの情報を確認します。");
        }
    }
}
