package xyz.acrylicstyle.hackReport.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import util.Collection;
import xyz.acrylicstyle.hackReport.HackReport;
import xyz.acrylicstyle.hackReport.utils.Utils;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class ReportGui implements InventoryHolder, Listener {
    private volatile UUID uuid = null;
    private final Collection<UUID, AtomicInteger> pages = new Collection<>();

    public ReportGui prepare(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    private Inventory setItems() {
        int page = pages.getOrDefault(uuid, new AtomicInteger(1)).get();
        Inventory inventory = Bukkit.createInventory(this, 54, ChatColor.GREEN + "プレイヤーの行動を通報 - ページ" + page);
        Utils.getOnlinePlayers(uuid).foreach((player, index) -> {
            if (index >= 44*(page-1) && index <= 44*page) {
                ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
                skullMeta.setOwner(player.getName());
                skullMeta.setDisplayName(ChatColor.RED + player.getName() + ChatColor.YELLOW + "を通報する");
                skullMeta.setLore(Collections.singletonList(ChatColor.RED + "注意: 虚偽の通報をすると通報権限の剥奪や処罰が行われる可能性があります。"));
                skull.setItemMeta(skullMeta);
                inventory.setItem(index-(44*(page-1)), skull);
            }
        });
        inventory.setItem(45, Utils.getItemStack(Material.ARROW, ChatColor.YELLOW + "←前のページ"));
        inventory.setItem(49, Utils.getItemStack(Material.BARRIER, ChatColor.YELLOW + "閉じる"));
        inventory.setItem(53, Utils.getItemStack(Material.ARROW, ChatColor.YELLOW + "次のページ→"));
        return inventory;
    }

    @Override
    public Inventory getInventory() {
        return setItems();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() != this) return;
        if (e.getClickedInventory() == null || e.getClickedInventory().getHolder() != this) return;
        e.setCancelled(true);
        Player p = (Player) e.getWhoClicked();
        if (e.getSlot() < 45) {
            int page = pages.getOrDefault(uuid, new AtomicInteger(1)).get();
            Utils.getOnlinePlayers(uuid).foreach((player, index) -> {
                if (index >= 44*(page-1) && index <= 44*page) {
                    if (index-(44*(page-1)) == e.getSlot()) {
                        p.openInventory(HackReport.REPORT_CONFIRM_GUI.prepare(p, player).getInventory());
                    }
                }
            });
            return;
        }
        if (e.getSlot() == 45) {
            if (!pages.containsKey(p.getUniqueId())) pages.add(p.getUniqueId(), new AtomicInteger(1));
            if (pages.get(p.getUniqueId()).get() > 1) pages.get(p.getUniqueId()).decrementAndGet();
            p.openInventory(getInventory());
        } else if (e.getSlot() == 49) {
            p.closeInventory();
        } else if (e.getSlot() == 53) {
            if (!pages.containsKey(p.getUniqueId())) pages.add(p.getUniqueId(), new AtomicInteger(1));
            pages.get(p.getUniqueId()).incrementAndGet();
            p.openInventory(getInventory());
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
        } catch (ReflectiveOperationException ignore) {}
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        pages.remove(e.getPlayer().getUniqueId()); // there is no inventory holder check, intentionally.
    }
}
