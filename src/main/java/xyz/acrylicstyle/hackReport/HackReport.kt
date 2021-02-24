package xyz.acrylicstyle.hackReport

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.plugin.java.JavaPlugin
import util.Collection
import util.CollectionList
import util.CollectionSet
import xyz.acrylicstyle.hackReport.commands.CommandLogCommand
import xyz.acrylicstyle.hackReport.commands.IgnoreCommand
import xyz.acrylicstyle.hackReport.commands.IgnoreWordCommand
import xyz.acrylicstyle.hackReport.commands.NameChangesCommand
import xyz.acrylicstyle.hackReport.commands.OpChatCommand
import xyz.acrylicstyle.tomeito_api.TomeitoAPI
import xyz.acrylicstyle.tomeito_api.providers.ConfigProvider
import xyz.acrylicstyle.tomeito_api.utils.Log
import java.util.UUID

class HackReport : JavaPlugin(), Listener {
    override fun onLoad() {
        instance = this
        Thread {
            Log.info("Loading classes")
            val start = System.currentTimeMillis()
            preloadClass("xyz.acrylicstyle.hackReport.HackReport$1")
            preloadClass("xyz.acrylicstyle.hackReport.HackReport$2")
            preloadClass("xyz.acrylicstyle.hackReport.HackReport$3")
            preloadClass("xyz.acrylicstyle.hackReport.HackReport\$onDisable\$1")
            preloadClass("xyz.acrylicstyle.hackReport.HackReport\$onDisable\$2")
            preloadClass("xyz.acrylicstyle.hackReport.utils.Utils")
            preloadClass("xyz.acrylicstyle.hackReport.utils.Webhook")
            preloadClass("xyz.acrylicstyle.hackReport.commands.CommandLogCommand")
            preloadClass("xyz.acrylicstyle.hackReport.commands.IgnoreCommand")
            preloadClass("xyz.acrylicstyle.hackReport.commands.IgnoreCommand$1")
            preloadClass("xyz.acrylicstyle.hackReport.commands.IgnoreWordCommand")
            preloadClass("xyz.acrylicstyle.hackReport.commands.IgnoreWordCommand$1")
            preloadClass("xyz.acrylicstyle.hackReport.commands.NameChangesCommand")
            preloadClass("xyz.acrylicstyle.hackReport.commands.NameChangesCommand$1")
            preloadClass("xyz.acrylicstyle.hackReport.commands.OpChatCommand")
            val end = System.currentTimeMillis()
            Log.info("Loaded classes in ${end - start}ms")
        }.start()
    }

    private fun preloadClass(clazz: String) {
        try {
            Class.forName(clazz)
        } catch (ignore: ClassNotFoundException) {}
    }

    override fun onEnable() {
        Log.info("Loading configuration")
        Log.info("Registering commands")
        TomeitoAPI.registerCommand("ignore", IgnoreCommand())
        TomeitoAPI.registerCommand("ignoreword", IgnoreWordCommand())
        TomeitoAPI.registerCommand("opchat", OpChatCommand())
        TomeitoAPI.registerCommand("commandlog", CommandLogCommand())
        TomeitoAPI.registerCommand("namechanges", NameChangesCommand())
        Log.info("Registering events")
        Bukkit.getPluginManager().registerEvents(this, this)
        Log.info("Enabled HackReport")
    }

    @EventHandler
    fun onPlayerCommandPreprocess(e: PlayerCommandPreprocessEvent) {
        Bukkit.getOnlinePlayers().stream().filter { obj: Player -> obj.isOp }.forEach { player: Player -> if (commandLog.contains(player.uniqueId)) player.sendMessage(ChatColor.GRAY.toString() + "[CMD] " + e.player.name + " sent command: " + e.message) }
        if (e.player.isOp) return
        if (e.message.startsWith("/tell ")
            || e.message.startsWith("/w ")
            || e.message.startsWith("/msg ")
            || e.message.startsWith("/t ")
            || e.message.startsWith("/m ")
            || e.message.startsWith("/message ")) {
            val p = e.message.split("\\s+".toRegex()).toTypedArray()[1]
            val player = Bukkit.getPlayer(p) ?: return
            if (IgnoreCommand.isPlayerIgnored(e.message, player, e.player)) {
                e.player.sendMessage(ChatColor.RED.toString() + "You cannot send private message to this player.")
                e.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onAsyncPlayerChat(e: AsyncPlayerChatEvent) {
        e.player.address.address.hostAddress
        if (e.player.isOp && opChat.contains(e.player.uniqueId)) {
            e.isCancelled = true
            OpChatCommand.Do(e.player.name, e.message)
            return
        }
        if (e.player.isOp) return
        e.recipients.removeIf { player: Player -> IgnoreCommand.isPlayerIgnored(e.message, player, e.player) }
    }

    companion object {
        @JvmField
        val opChat = CollectionSet<UUID>()
        @JvmField
        val commandLog = CollectionList<UUID>()
        @JvmStatic
        var instance: HackReport? = null
            private set
        const val CAP = 1000L

        private val cachedConfig = Collection<UUID, ConfigProvider>()

        fun getPlayerConfig(uuid: UUID): ConfigProvider {
            val cache = cachedConfig[uuid]
            if (cache != null) return cache
            val config = ConfigProvider.getConfig("./plugins/HackReport/players/$uuid.yml")
            cachedConfig[uuid] = config
            return config
        }
    }
}
