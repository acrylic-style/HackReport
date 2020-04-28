package xyz.acrylicstyle.hackReport.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.acrylicstyle.tomeito_api.command.Command;
import xyz.acrylicstyle.tomeito_api.command.PlayerCommandExecutor;

@Command("hackreport")
public class HackReportCommand extends PlayerCommandExecutor {
    @Override
    public void onCommand(Player player, String[] args) {
        player.sendMessage(ChatColor.YELLOW + " - /report [プレイヤー] [理由] " + ChatColor.GRAY + "- " + ChatColor.AQUA + "プレイヤーを通報します。");
        player.sendMessage(ChatColor.YELLOW + " - /ignore add <プレイヤー>" + ChatColor.GRAY + "- " + ChatColor.AQUA + "Ignoreリストに追加して、指定したプレイヤーのチャットを非表示にします。");
        player.sendMessage(ChatColor.YELLOW + " - /ignore remove <プレイヤー>" + ChatColor.GRAY + "- " + ChatColor.AQUA + "Ignoreリストからプレイヤーを削除します。");
        player.sendMessage(ChatColor.YELLOW + " - /ignore list [ページ]" + ChatColor.GRAY + "- " + ChatColor.AQUA + "Ignoreリストを表示します。");
        if (player.isOp()) {
            player.sendMessage(ChatColor.YELLOW + " - /reports " + ChatColor.GRAY + "- " + ChatColor.AQUA + "通報一覧を表示します。");
            player.sendMessage(ChatColor.YELLOW + " - /player <プレイヤー> " + ChatColor.GRAY + "- " + ChatColor.AQUA + "プレイヤーの情報を確認します。");
        }
    }
}