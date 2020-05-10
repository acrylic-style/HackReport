package xyz.acrylicstyle.hackReport.api.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GlobalChatEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    @NotNull
    protected final Player player;
    @NotNull
    protected final List<Player> recipients;

    public GlobalChatEvent(@NotNull Player player, @Nullable List<Player> recipients) {
        super(!Bukkit.isPrimaryThread());
        this.player = player;
        this.recipients = recipients == null ? new ArrayList<>(Bukkit.getOnlinePlayers()) : recipients;
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    @NotNull
    public List<Player> getRecipients() {
        return recipients;
    }
}
