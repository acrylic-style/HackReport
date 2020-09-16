package xyz.acrylicstyle.hackReport.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class HackReportPlayer {
    @NotNull private final UUID uuid;
    @Nullable private final Player player;

    public HackReportPlayer(@NotNull UUID uuid) {
        this.uuid = uuid;
        this.player = Bukkit.getPlayer(uuid);
    }

    @NotNull
    public UUID getUniqueId() {
        return uuid;
    }

    @Nullable
    public Player getPlayer() {
        return player;
    }

    public boolean isOnline() {
        return player != null && player.isOnline();
    }

    public double getHealth() {
        return player == null ? 0D : player.getHealth();
    }

    public int getFoodLevel() {
        return player == null ? 0 : player.getFoodLevel();
    }

    public boolean isFlying() {
        return player != null && player.isFlying();
    }
}
