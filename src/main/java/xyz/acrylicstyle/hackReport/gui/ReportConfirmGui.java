package xyz.acrylicstyle.hackReport.gui;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.ServerOperator;
import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.hackReport.HackReport;
import xyz.acrylicstyle.hackReport.utils.InventoryUtils;
import xyz.acrylicstyle.hackReport.utils.Utils;
import xyz.acrylicstyle.tomeito_api.sounds.Sound;

import java.util.Collections;

public class ReportConfirmGui implements InventoryHolder, Listener {
    private volatile Player uuid = null;
    private volatile Player target = null;

    public ReportConfirmGui register() {
        Bukkit.getPluginManager().registerEvents(this, HackReport.getInstance());
        return this;
    }

    @NotNull
    public ReportConfirmGui prepare(@NotNull Player reporter, @NotNull Player target) {
        this.uuid = reporter;
        this.target = target;
        return this;
    }

    @NotNull
    private Inventory getItems() {
        if (uuid == null || target == null) throw new NullPointerException("You must call #prepare first.");
        Inventory inventory = Bukkit.createInventory(this, 27, ChatColor.YELLOW + "確認");
        ItemStack green = new ItemStack(Material.WOOL, 1, (short) 5);
        ItemMeta greenMeta = green.getItemMeta();
        Validate.notNull(greenMeta, "Meta cannot be null");
        greenMeta.setDisplayName(ChatColor.RED + "通報する");
        greenMeta.setLore(Collections.singletonList(ChatColor.RED + "注意: 虚偽の通報をすると通報権限の剥奪や処罰が行われる可能性があります。"));
        green.setItemMeta(greenMeta);
        ItemStack red = new ItemStack(Material.WOOL, 1, (short) 14);
        ItemMeta redMeta = red.getItemMeta();
        Validate.notNull(redMeta, "Meta cannot be null");
        redMeta.setDisplayName(ChatColor.RED + "キャンセル");
        red.setItemMeta(redMeta);
        inventory.setItem(11, green);
        inventory.setItem(15, red);
        return new InventoryUtils(inventory).fillEmptySlotsWithGlass().getInventory();
    }

    @Override
    public Inventory getInventory() {
        return getItems();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() != this) return;
        if (e.getClickedInventory() == null || e.getClickedInventory().getHolder() != this) return;
        e.setCancelled(true);
        Player player = (Player) e.getWhoClicked();
        if (e.getSlot() == 11) {
            Utils.getOnlinePlayers().filter(ServerOperator::isOp).forEach(p -> {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 0);
                p.sendMessage(ChatColor.GREEN + "通報: " + ChatColor.RED + target.getName() + ChatColor.GREEN + " from " + ChatColor.YELLOW + player.getName());
            });
            HackReport.getPlayerInfo(target.getName(), target.getUniqueId()).increaseReports();
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 2);
            player.sendMessage(ChatColor.GREEN + "通報が完了しました。");
            player.closeInventory();
        } else if (e.getSlot() == 15) {
            e.getWhoClicked().openInventory(new ReportGui().register().prepare(e.getWhoClicked().getUniqueId()).getInventory());
        }
    }

    @EventHandler
    public void onInventoryDragEvent(InventoryDragEvent e) {
        if (e.getInventory().getHolder() != this) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryEvent(InventoryEvent e) {
        if (e.getInventory().getHolder() != this) return;
        try {
            e.getClass().getMethod("setCancelled", boolean.class).invoke(e, true);
        } catch (ReflectiveOperationException ex) { throw new RuntimeException(ex); }
    }
}
