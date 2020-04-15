package xyz.acrylicstyle.hackReport.gui;

import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.apache.commons.lang.Validate;
import org.bukkit.BanList;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.hackReport.HackReport;
import xyz.acrylicstyle.hackReport.utils.InventoryUtils;
import xyz.acrylicstyle.hackReport.utils.Utils;

import java.util.UUID;

public class PlayerActionGui implements InventoryHolder, Listener {
    private volatile Player uuid = null;
    private volatile UUID targetUUID = null;
    private volatile String targetName = null;

    @NotNull
    public PlayerActionGui prepare(@NotNull Player reporter, @NotNull String targetName, @NotNull UUID targetUUID) {
        this.uuid = reporter;
        this.targetName = targetName;
        this.targetUUID = targetUUID;
        return this;
    }

    @NotNull
    private Inventory getItems() {
        if (uuid == null || targetName == null || targetUUID == null) throw new NullPointerException("You must call #prepare first.");
        Inventory inventory = Bukkit.createInventory(this, 27, ChatColor.YELLOW + "確認");
        ItemStack ban = new ItemStack(Material.WOOL, 1, (short) 5);
        ItemMeta banMeta = ban.getItemMeta();
        Validate.notNull(banMeta, "Meta cannot be null");
        banMeta.setDisplayName(ChatColor.GREEN + "BANする");
        ban.setItemMeta(banMeta);
        ItemStack kick = new ItemStack(Material.WOOL, 1, (short) 5);
        ItemMeta kickMeta = kick.getItemMeta();
        Validate.notNull(kickMeta, "Meta cannot be null");
        kickMeta.setDisplayName(ChatColor.GREEN + "Kickする");
        kick.setItemMeta(kickMeta);
        ItemStack revoke = new ItemStack(Material.WOOL, 1, (short) 5);
        ItemMeta revokeMeta = revoke.getItemMeta();
        Validate.notNull(revokeMeta, "Meta cannot be null");
        revokeMeta.setDisplayName(ChatColor.GREEN + "通報者の権限を剥奪する");
        revoke.setItemMeta(revokeMeta);
        ItemStack unrevoke = new ItemStack(Material.WOOL, 1, (short) 5);
        ItemMeta unrevokeMeta = revoke.getItemMeta();
        Validate.notNull(unrevokeMeta, "Meta cannot be null");
        unrevokeMeta.setDisplayName(ChatColor.GREEN + "剥奪した通報者の権限を戻す");
        unrevoke.setItemMeta(unrevokeMeta);
        ItemStack red = new ItemStack(Material.WOOL, 1, (short) 14);
        ItemMeta redMeta = red.getItemMeta();
        Validate.notNull(redMeta, "Meta cannot be null");
        redMeta.setDisplayName(ChatColor.RED + "キャンセル");
        red.setItemMeta(redMeta);
        inventory.setItem(11, ban);
        if (HackReport.luckPerms == null) {
            inventory.setItem(13, kick);
        } else {
            inventory.setItem(12, kick);
            inventory.setItem(13, revoke);
            inventory.setItem(14, unrevoke);
        }
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
            Bukkit.getBanList(BanList.Type.NAME).addBan(targetName, null, null, null);
            Player player2 = Bukkit.getPlayer(targetUUID);
            if (player2 != null) player2.kickPlayer(null);
            Utils.getOnlinePlayers().filter(Player::isOp).forEach(p -> p.sendMessage(ChatColor.GREEN + "[HackReport]" + ChatColor.GOLD + player.getName() + ChatColor.GREEN + "が" + ChatColor.RED + targetName + ChatColor.GREEN + "をBANしました。"));
            player.playSound(player.getLocation(), Utils.BLOCK_NOTE_PLING, 100, 2);
            player.sendMessage(ChatColor.GREEN + "プレイヤーをBANしました。");
            player.closeInventory();
        }
        if (e.getSlot() == (HackReport.luckPerms == null ? 13 : 12)) {
            Player player2 = Bukkit.getPlayer(targetUUID);
            if (player2 == null) {
                player.sendMessage(ChatColor.RED + "プレイヤーは現在オンラインではありません。");
                return;
            }
            player2.kickPlayer(null);
            player.playSound(player.getLocation(), Utils.BLOCK_NOTE_PLING, 100, 2);
            player.sendMessage(ChatColor.GREEN + "プレイヤーをKickしました。");
            player.closeInventory();
        }
        if (HackReport.luckPerms != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (e.getSlot() == 13) {
                        User user = HackReport.luckPerms.getUserManager().loadUser(targetUUID).join();
                        user.data().add(Node.builder("-hackreport.report").build());
                        HackReport.luckPerms.getUserManager().saveUser(user);
                        player.playSound(player.getLocation(), Utils.BLOCK_NOTE_PLING, 100, 2);
                        player.sendMessage(ChatColor.RED + targetName + ChatColor.GREEN + "の通報権限を剥奪しました。");
                        player.closeInventory();
                    } else if (e.getSlot() == 14) {
                        User user = HackReport.luckPerms.getUserManager().loadUser(targetUUID).join();
                        user.data().remove(Node.builder("-hackreport.report").build());
                        HackReport.luckPerms.getUserManager().saveUser(user);
                        player.playSound(player.getLocation(), Utils.BLOCK_NOTE_PLING, 100, 2);
                        player.sendMessage(ChatColor.RED + targetName + ChatColor.GREEN + "の通報権限の剥奪を取り消しました。");
                        player.closeInventory();
                    }
                }
            }.runTaskAsynchronously(HackReport.getInstance());
        }
        if (e.getSlot() == 15) {
            e.getWhoClicked().openInventory(HackReport.REPORT_GUI.prepare(e.getWhoClicked().getUniqueId()).getInventory());
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
