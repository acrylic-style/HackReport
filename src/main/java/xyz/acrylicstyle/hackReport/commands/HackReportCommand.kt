package xyz.acrylicstyle.hackReport.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.acrylicstyle.hackReport.HackReport;
import xyz.acrylicstyle.tomeito_api.command.PlayerCommandExecutor;

public class HackReportCommand extends PlayerCommandExecutor {
    @Override
    public void onCommand(Player player, String[] args) {
        if (player.isOp() && args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            HackReport.config.reload();
            player.sendMessage(ChatColor.GREEN + "設定を再読み込みしました。");
            return;
        }
        player.sendMessage(ChatColor.YELLOW + " - /report [プレイヤー] [理由] " + ChatColor.GRAY + "- " + ChatColor.AQUA + "プレイヤーを通報します。");
        player.sendMessage(ChatColor.YELLOW + " - /ignore add <プレイヤー>" + ChatColor.GRAY + "- " + ChatColor.AQUA + "Ignoreリストに追加して、指定したプレイヤーのチャットを非表示にします。");
        player.sendMessage(ChatColor.YELLOW + " - /ignore remove <プレイヤー>" + ChatColor.GRAY + "- " + ChatColor.AQUA + "Ignoreリストからプレイヤーを削除します。");
        player.sendMessage(ChatColor.YELLOW + " - /ignore list [ページ]" + ChatColor.GRAY + "- " + ChatColor.AQUA + "Ignoreリストを表示します。");
        if (player.isOp()) {
            player.sendMessage(ChatColor.YELLOW + " - /reports " + ChatColor.GRAY + "- " + ChatColor.AQUA + "通報一覧を表示します。");
            player.sendMessage(ChatColor.YELLOW + " - /player <プレイヤー> " + ChatColor.GRAY + "- " + ChatColor.AQUA + "プレイヤーの情報を確認します。");
            player.sendMessage(ChatColor.YELLOW + " - /mute <プレイヤー> " + ChatColor.GRAY + "- " + ChatColor.AQUA + "プレイヤーをミュート/ミュート解除します。");
            player.sendMessage(ChatColor.YELLOW + " - /oc [メッセージ] " + ChatColor.GRAY + "- " + ChatColor.AQUA + "OPChatでチャットする/OPChatモードに切り替えます。");
            player.sendMessage(ChatColor.YELLOW + " - /commandlog " + ChatColor.GRAY + "- " + ChatColor.AQUA + "コマンドログを表示するようにします。");
            player.sendMessage(ChatColor.YELLOW + " - /hackreport reload " + ChatColor.GRAY + "- " + ChatColor.AQUA + "設定を再読み込みします。");
            player.sendMessage(ChatColor.YELLOW + " - /muteall " + ChatColor.GRAY + "- " + ChatColor.AQUA + "全員のチャット(OP以外)をミュートにします。");
        }
    }
}
