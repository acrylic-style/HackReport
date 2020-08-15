package xyz.acrylicstyle.hackReport.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.acrylicstyle.hackReport.HackReport;
import xyz.acrylicstyle.joinChecker.utils.Utils;
import xyz.acrylicstyle.tomeito_api.command.PlayerCommandExecutor;
import xyz.acrylicstyle.tomeito_api.gui.ClickableItem;

public class PlayerCheckerCommand extends PlayerCommandExecutor {
    public static final ItemStack ITEM = ClickableItem.of(Material.GOLD_NUGGET, ChatColor.GREEN + "プレイヤーチェッカー", e -> {}).getItemStack();

    @Override
    public void onCommand(Player player, String[] args) {
        new Thread(() -> {
            if (Utils.modCheck(player)) return;
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.getInventory().addItem(ITEM);
                    player.sendMessage(ChatColor.YELLOW + "プレイヤーチェッカーをインベントリに追加しました。");
                }
            }.runTask(HackReport.getInstance());
        }).start();
    }
}
