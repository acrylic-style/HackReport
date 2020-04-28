package xyz.acrylicstyle.hackReport;

import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import util.Collection;
import util.CollectionList;
import xyz.acrylicstyle.hackReport.commands.*;
import xyz.acrylicstyle.hackReport.gui.*;
import xyz.acrylicstyle.hackReport.utils.PlayerInfo;
import xyz.acrylicstyle.hackReport.utils.ReportDetails;
import xyz.acrylicstyle.tomeito_api.TomeitoAPI;
import xyz.acrylicstyle.tomeito_api.providers.ConfigProvider;
import xyz.acrylicstyle.tomeito_api.utils.Log;

import java.util.UUID;

public class HackReport extends JavaPlugin implements Listener {
    public static final ReportGui REPORT_GUI = new ReportGui();
    public static final ReportConfirmGui REPORT_CONFIRM_GUI = new ReportConfirmGui();
    public static final Collection<UUID, PlayerInfo> PLAYERS = new Collection<>();
    public static final CollectionList<ReportDetails> REPORTS = new CollectionList<>();
    public static final ReportListGui REPORT_LIST_GUI = new ReportListGui();
    public static final ReportList2Gui REPORT_LIST_2_GUI = new ReportList2Gui();
    public static final PlayerActionGui PLAYER_ACTION_GUI = new PlayerActionGui();
    public static ConfigProvider config = null;

    public static LuckPerms luckPerms = null;

    private static HackReport instance;

    public static HackReport getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;
    }

    public static PlayerInfo getPlayerInfo(String name, UUID uuid) {
        if (!PLAYERS.containsKey(uuid)) PLAYERS.add(uuid, new PlayerInfo(name, uuid));
        return PLAYERS.get(uuid);
    }

    @Override
    public void onEnable() {
        Log.info("Loading configuration");
        config = ConfigProvider.initWithoutException("./plugins/HackReport/config.yml");
        Log.info("Registering commands");
        TomeitoAPI.registerCommand("hackreport", new HackReportCommand());
        TomeitoAPI.registerCommand("player", new PlayerCommand());
        TomeitoAPI.registerCommand("report", new ReportCommand());
        TomeitoAPI.registerCommand("reports", new ReportsCommand());
        TomeitoAPI.registerCommand("ignore", new IgnoreCommand());
        Log.info("Registering events");
        Bukkit.getPluginManager().registerEvents(REPORT_GUI, this);
        Bukkit.getPluginManager().registerEvents(REPORT_CONFIRM_GUI, this);
        Bukkit.getPluginManager().registerEvents(REPORT_LIST_GUI, this);
        Bukkit.getPluginManager().registerEvents(REPORT_LIST_2_GUI, this);
        Bukkit.getPluginManager().registerEvents(PLAYER_ACTION_GUI, this);
        Bukkit.getPluginManager().registerEvents(this, this);
        new BukkitRunnable() {
            @Override
            public void run() {
                RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
                if (provider != null) luckPerms = provider.getProvider();
            }
        }.runTaskLater(this, 1);
        Log.info("Enabled HackReport");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player killer = e.getEntity().getKiller();
        if (killer != null) {
            getPlayerInfo(killer.getName(), killer.getUniqueId()).increaseKills();
        }
        getPlayerInfo(e.getEntity().getName(), e.getEntity().getUniqueId()).increaseDeaths();
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
        e.getRecipients().removeIf((player -> IgnoreCommand.isPlayerIgnored(player.getUniqueId(), e.getPlayer().getUniqueId())));
    }
}
