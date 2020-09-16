package xyz.acrylicstyle.hackReport.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.acrylicstyle.api.MojangAPI;
import xyz.acrylicstyle.hackReport.gui.PlayerActionGui;
import xyz.acrylicstyle.tomeito_api.command.PlayerCommandExecutor;

import java.util.UUID;

public class PlayerCommand extends PlayerCommandExecutor {
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
        player.openInventory(new PlayerActionGui().register().prepare(player, args[0], uuid).getInventory());
    }
}
