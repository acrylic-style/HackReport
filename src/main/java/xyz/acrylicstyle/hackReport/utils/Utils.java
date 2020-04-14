package xyz.acrylicstyle.hackReport.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import util.CollectionList;
import util.ICollectionList;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class Utils {
    public static final Sound BLOCK_NOTE_PLING;

    static {
        if (ICollectionList.asList(Sound.values()).map(Enum::name).map((Function<String, String>) String::toLowerCase).contains("BLOCK_NOTE_PLING")) {
            BLOCK_NOTE_PLING = Sound.valueOf("BLOCK_NOTE_PLING");
        } else {
            BLOCK_NOTE_PLING = Sound.valueOf("NOTE_PLING");
        }
    }

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

    public static ItemStack getItemStack(Material material, String displayName, String... lore) {
        return getItemStack(material, displayName, Arrays.asList(lore));
    }

    public static ItemStack getItemStack(Material material, String displayName) {
        return getItemStack(material, displayName, (List<String>) null);
    }
}
