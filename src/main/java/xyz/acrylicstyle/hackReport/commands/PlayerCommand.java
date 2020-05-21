package xyz.acrylicstyle.hackReport.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.acrylicstyle.api.MojangAPI;
import xyz.acrylicstyle.hackReport.HackReport;
import xyz.acrylicstyle.tomeito_api.command.PlayerOpCommandExecutor;

import java.util.UUID;

public class PlayerCommand extends PlayerOpCommandExecutor {
    @Override
    public void onCommand(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "/player <player>");
            return;
        }
        String p = args[0];
        UUID uuid = MojangAPI.getUniqueId(p);
        if (uuid == null) {
            player.sendMessage(ChatColor.RED + "プレイヤーが見つかりません。");
            player.sendMessage(ChatColor.RED + "/player <player>");
            return;
        }
        player.openInventory(HackReport.PLAYER_ACTION_GUI.prepare(player, args[0], uuid).getInventory());
    }
}
