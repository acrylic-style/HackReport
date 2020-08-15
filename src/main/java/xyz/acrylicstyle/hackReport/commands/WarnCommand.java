package xyz.acrylicstyle.hackReport.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import util.CollectionList;
import util.ICollectionList;
import xyz.acrylicstyle.api.MojangAPI;
import xyz.acrylicstyle.hackReport.HackReport;
import xyz.acrylicstyle.joinChecker.utils.Utils;

import java.util.UUID;

public class WarnCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender player, Command command, String label, String[] args) {
        new Thread(() -> {
            if (Utils.modCheck(player)) return;
            if (args.length == 0) {
                player.sendMessage(ChatColor.RED + "/warn <Player> [理由] " + ChatColor.GRAY + "- " + ChatColor.AQUA + "プレイヤーを警告します。");
                return;
            }
            UUID uuid = MojangAPI.getUniqueId(args[0]);
            if (uuid == null) {
                player.sendMessage(ChatColor.RED + "プレイヤーが見つかりません。");
                return;
            }
            if (args.length < 2) {
                HackReport.warnQueue.remove(uuid);
                player.sendMessage(ChatColor.GREEN + "警告リストから削除しました。");
                return;
            }
            CollectionList<String> list = ICollectionList.asList(args);
            list.shift();
            HackReport.warnQueue.add(uuid, ChatColor.translateAlternateColorCodes('&', list.join(" ")));
            player.sendMessage(ChatColor.GREEN + "プレイヤーを警告しました: " + list.join(" "));
            player.sendMessage(ChatColor.GRAY + "プレイヤーがオフラインの場合は次回参加時に警告されます。");
        }).start();
        return true;
    }
}
