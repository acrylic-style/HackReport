package xyz.acrylicstyle.hackReport.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import util.CollectionList;
import xyz.acrylicstyle.hackReport.HackReport;

import java.util.List;
import java.util.UUID;

public class Utils {
    public static CollectionList<Player> getOnlinePlayers() {
        CollectionList<Player> players = new CollectionList<>();
        players.addAll(Bukkit.getOnlinePlayers());
        return players;
    }

    public static CollectionList<Player> getOnlinePlayers(UUID you) {
        CollectionList<Player> players = new CollectionList<>();
        players.addAll(Bukkit.getOnlinePlayers());
        return players.filter(p -> !p.getUniqueId().equals(you) && !p.isOp());
    }

    public static ItemStack getItemStack(Material material, String displayName, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (displayName != null) meta.setDisplayName(displayName);
        if (lore != null) meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getItemStack(Material material, String displayName) {
        return getItemStack(material, displayName, null);
    }

    @Nullable
    public static Webhook getWebhook() {
        String url = HackReport.config.getString("webhook");
        if (url == null) return null;
        Webhook webhook = new Webhook(url);
        webhook.setUsername("HackReport on " + Bukkit.getVersion());
        return webhook;
    }
}
