package xyz.acrylicstyle.hackReport.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.acrylicstyle.hackReport.HackReport;
import xyz.acrylicstyle.tomeito_api.command.PlayerCommandExecutor;

public class CommandLogCommand extends PlayerCommandExecutor {
    @Override
    public void onCommand(Player player, String[] args) {
        if (HackReport.commandLog.contains(player.getUniqueId())) {
            HackReport.commandLog.remove(player.getUniqueId());
            player.sendMessage(ChatColor.YELLOW + "コマンドログを非表示にしました。");
        } else {
            HackReport.commandLog.add(player.getUniqueId());
            player.sendMessage(ChatColor.YELLOW + "コマンドログを表示するようにしました。");
        }
    }
}
