package xyz.acrylicstyle.hackReport.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.acrylicstyle.api.MojangAPI;
import xyz.acrylicstyle.hackReport.HackReport;
import xyz.acrylicstyle.tomeito_api.command.Command;
import xyz.acrylicstyle.tomeito_api.command.PlayerOpCommandExecutor;

import java.util.UUID;

@Command("player")
public class PlayerCommand extends PlayerOpCommandExecutor {
    @Override
    public void onCommand(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "/player <player>");
            return;
        }
        String p = args[0];
        if (player.getName().equalsIgnoreCase(p)) {
            player.sendMessage(ChatColor.RED + "自分自身を操作することはできません。");
            return;
        }
        UUID uuid = MojangAPI.getUniqueId(p);
        if (uuid == null) {
            player.sendMessage(ChatColor.RED + "プレイヤーが見つかりません。");
            player.sendMessage(ChatColor.RED + "/player <player>");
            return;
        }
        if (player.getUniqueId().equals(uuid)) {
            player.sendMessage(ChatColor.RED + "自分自身を操作することはできません。");
            return;
        }
        if (Bukkit.getOfflinePlayer(uuid).isOp()) {
            player.sendMessage(ChatColor.RED + "OPを操作することはできません。");
            return;
        }
        player.openInventory(HackReport.PLAYER_ACTION_GUI.prepare(player, args[0], uuid).getInventory());
    }
}
