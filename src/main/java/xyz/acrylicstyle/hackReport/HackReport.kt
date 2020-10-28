package xyz.acrylicstyle.hackReport

import net.luckperms.api.LuckPerms
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.scheduler.BukkitRunnable
import util.Collection
import util.CollectionList
import util.CollectionSet
import util.ReflectionHelper
import util.Watchdog
import util.reflect.Ref
import xyz.acrylicstyle.api.v1_8_R1.MojangAPI
import xyz.acrylicstyle.craftbukkit.v1_8_R3.util.CraftUtils
import xyz.acrylicstyle.hackReport.commands.AMuteCommand
import xyz.acrylicstyle.hackReport.commands.CommandLogCommand
import xyz.acrylicstyle.hackReport.commands.HackReportCommand
import xyz.acrylicstyle.hackReport.commands.IgnoreCommand
import xyz.acrylicstyle.hackReport.commands.LookupMuteCommand
import xyz.acrylicstyle.hackReport.commands.ModChatCommand
import xyz.acrylicstyle.hackReport.commands.MuteAllCommand
import xyz.acrylicstyle.hackReport.commands.MuteCommand
import xyz.acrylicstyle.hackReport.commands.MuteListCommand
import xyz.acrylicstyle.hackReport.commands.MuteTellCommand
import xyz.acrylicstyle.hackReport.commands.NameChangesCommand
import xyz.acrylicstyle.hackReport.commands.OpChatCommand
import xyz.acrylicstyle.hackReport.commands.PlayerCheckerCommand
import xyz.acrylicstyle.hackReport.commands.PlayerCommand
import xyz.acrylicstyle.hackReport.commands.ReportCommand
import xyz.acrylicstyle.hackReport.commands.ReportsCommand
import xyz.acrylicstyle.hackReport.commands.UnwatchCommand
import xyz.acrylicstyle.hackReport.commands.VanishCommand
import xyz.acrylicstyle.hackReport.commands.WarnCommand
import xyz.acrylicstyle.hackReport.commands.WatchCommand
import xyz.acrylicstyle.hackReport.utils.ConnectionHolder
import xyz.acrylicstyle.hackReport.utils.ConnectionHolder.Companion.muteList
import xyz.acrylicstyle.hackReport.utils.PlayerInfo
import xyz.acrylicstyle.hackReport.utils.ReportDetails
import xyz.acrylicstyle.hackReport.utils.Utils.getHand
import xyz.acrylicstyle.hackReport.utils.Utils.getPing
import xyz.acrylicstyle.hackReport.utils.Webhook
import xyz.acrylicstyle.minecraft.v1_8_R1.Packet
import xyz.acrylicstyle.shared.NMSAPI
import xyz.acrylicstyle.shared.NameHistory
import xyz.acrylicstyle.tomeito_api.TomeitoAPI
import xyz.acrylicstyle.tomeito_api.events.player.EntityDamageByPlayerEvent
import xyz.acrylicstyle.tomeito_api.providers.ConfigProvider
import xyz.acrylicstyle.tomeito_api.sounds.Sound
import xyz.acrylicstyle.tomeito_api.utils.Log
import xyz.acrylicstyle.tomeito_api.utils.ReflectionUtil
import java.awt.Color
import java.lang.reflect.Method
import java.util.Objects
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Consumer
import java.util.function.Function
import java.util.stream.Collectors
import kotlin.math.roundToInt

class HackReport : JavaPlugin(), Listener {
    private val cps = Collection<UUID, AtomicInteger>()

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
            preloadClass("xyz.acrylicstyle.hackReport.utils.ReportDetails")
            preloadClass("xyz.acrylicstyle.hackReport.utils.Webhook")
            preloadClass("xyz.acrylicstyle.hackReport.utils.PlayerInfo")
            preloadClass("xyz.acrylicstyle.hackReport.utils.InventoryUtils")
            preloadClass("xyz.acrylicstyle.hackReport.utils.HackReportPlayer")
            preloadClass("xyz.acrylicstyle.hackReport.utils.ConnectionHolder")
            preloadClass("xyz.acrylicstyle.hackReport.utils.ConnectionHolder\$Companion")
            preloadClass("xyz.acrylicstyle.hackReport.gui.PlayerActionGui")
            preloadClass("xyz.acrylicstyle.hackReport.gui.PlayerActionGui$1")
            preloadClass("xyz.acrylicstyle.hackReport.gui.ReportConfirmGui")
            preloadClass("xyz.acrylicstyle.hackReport.gui.ReportGui")
            preloadClass("xyz.acrylicstyle.hackReport.gui.ReportList2Gui")
            preloadClass("xyz.acrylicstyle.hackReport.gui.ReportListGui")
            preloadClass("xyz.acrylicstyle.hackReport.commands.CommandLogCommand")
            preloadClass("xyz.acrylicstyle.hackReport.commands.HackReportCommand")
            preloadClass("xyz.acrylicstyle.hackReport.commands.IgnoreCommand")
            preloadClass("xyz.acrylicstyle.hackReport.commands.IgnoreCommand$1")
            preloadClass("xyz.acrylicstyle.hackReport.commands.ModChatCommand")
            preloadClass("xyz.acrylicstyle.hackReport.commands.MuteAllCommand")
            preloadClass("xyz.acrylicstyle.hackReport.commands.MuteCommand")
            preloadClass("xyz.acrylicstyle.hackReport.commands.MuteListCommand")
            preloadClass("xyz.acrylicstyle.hackReport.commands.NameChangesCommand")
            preloadClass("xyz.acrylicstyle.hackReport.commands.NameChangesCommand$1")
            preloadClass("xyz.acrylicstyle.hackReport.commands.OpChatCommand")
            preloadClass("xyz.acrylicstyle.hackReport.commands.PlayerCheckerCommand")
            preloadClass("xyz.acrylicstyle.hackReport.commands.PlayerCheckerCommand$1")
            preloadClass("xyz.acrylicstyle.hackReport.commands.PlayerCommand")
            preloadClass("xyz.acrylicstyle.hackReport.commands.ReportCommand")
            preloadClass("xyz.acrylicstyle.hackReport.commands.ReportCommand$1")
            preloadClass("xyz.acrylicstyle.hackReport.commands.ReportCommand$2")
            preloadClass("xyz.acrylicstyle.hackReport.commands.ReportsCommand")
            preloadClass("xyz.acrylicstyle.hackReport.commands.VanishCommand")
            preloadClass("xyz.acrylicstyle.hackReport.commands.WarnCommand")
            preloadClass("xyz.acrylicstyle.hackReport.commands.AMuteCommand")
            preloadClass("xyz.acrylicstyle.hackReport.struct.MuteList")
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
        HackReport.config = ConfigProvider("./plugins/HackReport/config.yml")
        val host = HackReport.config!!.getString("database.host")
        val name = HackReport.config!!.getString("database.name")
        val username = HackReport.config!!.getString("database.username")
        val password = HackReport.config!!.getString("database.password")
        if (host == null || name == null || username == null || password == null) {
            Log.info("Not using database")
        } else {
            Log.info("Database(mysql) will be used.")
            Thread {
                Log.with("HackReport").info("Connecting to the database")
                connection = ConnectionHolder(host, name, username, password)
                if (ConnectionHolder.ready) {
                    Log.with("HackReport").info("Connected to the database.")
                } else {
                    Log.with("HackReport").info("Something went wrong while connecting to the database.")
                }
            }.start()
        }
        Log.info("Registering commands")
        TomeitoAPI.registerCommand("hackreport", HackReportCommand())
        TomeitoAPI.registerCommand("player", PlayerCommand())
        TomeitoAPI.registerCommand("report", ReportCommand())
        TomeitoAPI.registerCommand("reports", ReportsCommand())
        TomeitoAPI.registerCommand("ignore", IgnoreCommand())
        TomeitoAPI.registerCommand("mute", MuteCommand())
        TomeitoAPI.registerCommand("amute", AMuteCommand())
        TomeitoAPI.registerCommand("mutetell", MuteTellCommand())
        TomeitoAPI.registerCommand("opchat", OpChatCommand())
        TomeitoAPI.registerCommand("modchat", ModChatCommand())
        TomeitoAPI.registerCommand("commandlog", CommandLogCommand())
        TomeitoAPI.registerCommand("muteall", MuteAllCommand())
        TomeitoAPI.registerCommand("namechanges", NameChangesCommand())
        TomeitoAPI.registerCommand("warn", WarnCommand())
        TomeitoAPI.registerCommand("mutelist", MuteListCommand())
        TomeitoAPI.registerCommand("lookupmute", LookupMuteCommand())
        TomeitoAPI.registerCommand("playerchecker", PlayerCheckerCommand())
        TomeitoAPI.registerCommand("/vanish", VanishCommand())
        TomeitoAPI.registerCommand("watch", WatchCommand())
        TomeitoAPI.registerCommand("unwatch", UnwatchCommand())
        Log.info("Registering events")
        Bukkit.getPluginManager().registerEvents(this, this)
        object : BukkitRunnable() {
            override fun run() {
                val provider = Bukkit.getServicesManager().getRegistration(LuckPerms::class.java)
                if (provider != null) luckPerms = provider.provider
            }
        }.runTaskLater(this, 1)
        object : BukkitRunnable() {
            override fun run() {
                warnQueue.clone().forEach { uuid: UUID, warn: String ->
                    val player = Bukkit.getPlayer(uuid) ?: return@forEach
                    player.playSound(player.location, Sound.BLOCK_NOTE_PLING, 100f, 0f)
                    player.sendMessage("")
                    player.sendMessage(ChatColor.GOLD.toString() + "===============================")
                    player.sendMessage("")
                    player.sendMessage(ChatColor.RED.toString() + "Adminからの警告があります。")
                    player.sendMessage(ChatColor.RED.toString() + "内容/理由: " + warn)
                    player.sendMessage("")
                    player.sendMessage(ChatColor.GOLD.toString() + "===============================")
                    player.sendMessage("")
                    warnQueue.remove(uuid)
                }
            }
        }.runTaskTimerAsynchronously(this, 20, 20)
        object : BukkitRunnable() {
            override fun run() {
                watchingPlayers.forEach { player, target ->
                    if (!player.isOnline) {
                        stopWatching(player)
                        return@forEach
                    }
                    if (!target.isOnline) {
                        player.sendActionbar("${ChatColor.GOLD}プレイヤーがオフラインになりました")
                        stopWatching(player)
                        return@forEach
                    }
                    if (!cps.containsKey(target.uniqueId)) cps[target.uniqueId] = AtomicInteger()
                    player.sendActionbar("${ChatColor.GOLD}${target.name} ${ChatColor.WHITE}| ${ChatColor.GREEN}体力: ${(target.health * 10).roundToInt().toDouble() / 10.toDouble()} ${ChatColor.WHITE}| ${ChatColor.GREEN}距離: ${(player.location.distance(target.location) * 10).roundToInt().toDouble() / 10.toDouble()} ${ChatColor.WHITE}| ${ChatColor.GREEN}Ping: ${target.getPing()}ms ${ChatColor.WHITE}| ${ChatColor.GREEN}CPS: ${cps[target.uniqueId]!!.get()}")
                }
            }
        }.runTaskTimer(this, 1, 1)
        Log.info("Enabled HackReport")
    }

    @Suppress("UNCHECKED_CAST")
    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        if (!cps.containsKey(e.player.uniqueId)) cps[e.player.uniqueId] = AtomicInteger()
        vanishedPlayers.map(Function { id: UUID? -> Bukkit.getPlayer(id) } as Function<UUID?, Player>).filter { obj: Player? -> Objects.nonNull(obj) }.filter { p: Player -> p.uniqueId != e.player.uniqueId }.forEach(Consumer { player: Player? -> e.player.hidePlayer(player) })
        if (vanishedPlayers.contains(e.player.uniqueId)) {
            Bukkit.getOnlinePlayers().forEach { p: Player -> p.hidePlayer(e.player) }
        }
        val skull = ItemStack(Material.SKULL_ITEM, 1, 3.toShort())
        val skullMeta = skull.itemMeta as SkullMeta
        skullMeta.owner = e.player.name // fetch first
        if (!e.player.isOp) {
            object : BukkitRunnable() {
                override fun run() {
                    val list = CollectionList<String>()
                    MojangAPI.getNameChanges(e.player.uniqueId).reverse().foreach { history: NameHistory, index: Int ->
                        if (index in 1..3) {
                            list.add(history.name)
                        }
                    }
                    if (list.size == 0) return
                    Bukkit.broadcastMessage(ChatColor.GRAY.toString() + "(also known as " + list.join(", ") + ")")
                }
            }.runTaskLaterAsynchronously(this, 1)
        }
        if (muteAll) {
            e.player.sendMessage(ChatColor.GOLD.toString() + "==============================")
            e.player.sendMessage("")
            e.player.sendMessage(ChatColor.YELLOW.toString() + "注意: 現在サーバー管理者によってサーバー全体のチャットが制限されています。")
            e.player.sendMessage("")
            e.player.sendMessage(ChatColor.GOLD.toString() + "==============================")
        }
        muteList.get(e.player.uniqueId, false).then { // remove mute when it expires
            if (it == null) return@then
            if (it.expiresAt == -1L) return@then
            if (System.currentTimeMillis() > it.expiresAt) {
                Log.info("Removing mute for ${e.player.name} automatically: mute expired")
                muteList.remove(e.player.uniqueId, false)
                e.player.sendMessage(ChatColor.GOLD.toString() + "==============================")
                e.player.sendMessage("${ChatColor.GREEN}一定時間が経過したためミュートが自動的に解除されました。")
                e.player.sendMessage(ChatColor.GOLD.toString() + "==============================")
            }
        }.queue()
        muteList.get(e.player.uniqueId, true).then {
            if (it == null) return@then
            if (it.expiresAt == -1L) return@then
            if (System.currentTimeMillis() > it.expiresAt) {
                Log.info("Removing tell mute for ${e.player.name} automatically: expired")
                muteList.remove(e.player.uniqueId, true)
                e.player.sendMessage(ChatColor.GOLD.toString() + "==============================")
                e.player.sendMessage("${ChatColor.GREEN}一定時間が経過したためTellミュートが自動的に解除されました。")
                e.player.sendMessage(ChatColor.GOLD.toString() + "==============================")
            }
        }.queue()
    }

    override fun onDisable() {
        Watchdog("HackReport#disable", {
            connection?.close()
        }, 1000 * 10, {
            Log.warn("Could not close database connection within 10 seconds!")
        }).start()
    }

    @EventHandler
    fun onPlayerCommandPreprocess(e: PlayerCommandPreprocessEvent) {
        if (e.message.startsWith("/kick ") && e.player.hasPermission("minecraft.command.kick")) {
            sendMessage(e, "`" + e.player.name + "`が`" + e.message.split("\\s+".toRegex()).toTypedArray()[1] + "`をKickしました")
        }
        if (e.message.startsWith("/pardon ") && e.player.hasPermission("minecraft.command.pardon")) {
            sendMessage(e, "`" + e.player.name + "`が`" + e.message.split("\\s+".toRegex()).toTypedArray()[1] + "`のBANを解除しました")
        }
        if (e.message.startsWith("/ban ") && e.player.hasPermission("minecraft.command.ban")) {
            sendMessage(e, "`" + e.player.name + "`が`" + e.message.split("\\s+".toRegex()).toTypedArray()[1] + "`をBANしました")
        }
        if (e.message.startsWith("/ban-ip ") && e.player.hasPermission("minecraft.command.ban-ip")) {
            sendMessage(e, "`" + e.player.name + "`が`" + e.message.split("\\s+".toRegex()).toTypedArray()[1] + "`をIP BANしました")
        }
        Bukkit.getOnlinePlayers().stream().filter { obj: Player -> obj.isOp }.forEach { player: Player -> if (commandLog.contains(player.uniqueId)) player.sendMessage(ChatColor.GRAY.toString() + "[CMD] " + e.player.name + " sent command: " + e.message) }
        if (e.player.isOp) return
        if (e.message.startsWith("/tell ")
            || e.message.startsWith("/w ")
            || e.message.startsWith("/msg ")
            || e.message.startsWith("/t ")
            || e.message.startsWith("/m ")
            || e.message.startsWith("/message ")
            || e.message.startsWith("/lunachat:tell ")
            || e.message.startsWith("/lunachat:w ")
            || e.message.startsWith("/lunachat:msg ")
            || e.message.startsWith("/lunachat:t ")
            || e.message.startsWith("/lunachat:m ")
            || e.message.startsWith("/lunachat:message ")) {
            val p = e.message.split("\\s+".toRegex()).toTypedArray()[1]
            val player = Bukkit.getPlayer(p) ?: return
            if (IgnoreCommand.isPlayerIgnored(player.uniqueId, e.player.uniqueId) || muteList.contains(e.player.uniqueId, true)) {
                e.player.sendMessage(ChatColor.RED.toString() + "このプレイヤーにプライベートメッセージを送信することはできません。")
                e.isCancelled = true
            }
        } else if (e.message.startsWith("/me ") || e.message.startsWith("/g ") || e.message.split("\\s+".toRegex()).toTypedArray()[0].endsWith(":g")) {
            if (muteAll || (ConnectionHolder.ready && muteList.contains(e.player.uniqueId, false))) {
                e.player.sendMessage(ChatColor.RED.toString() + "このコマンドを使用することはできません。")
                e.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        val killer = e.entity.killer
        if (killer != null) {
            getPlayerInfo(killer.name, killer.uniqueId).increaseKills()
        }
        getPlayerInfo(e.entity.name, e.entity.uniqueId).increaseDeaths()
    }

    @EventHandler
    fun onAsyncPlayerChat(e: AsyncPlayerChatEvent) {
        if (e.player.isOp && opChat.contains(e.player.uniqueId)) {
            e.isCancelled = true
            OpChatCommand.Do(e.player.name, e.message)
            return
        }
        if (modChat.contains(e.player.uniqueId)) {
            e.isCancelled = true
            ModChatCommand.Do(e.player.name, e.message)
            return
        }
        if (e.player.isOp) return
        if (muteAll || (ConnectionHolder.ready && muteList.contains(e.player.uniqueId, false))) {
            e.recipients.clear()
            return
        }
        e.recipients.removeIf { player: Player -> IgnoreCommand.isPlayerIgnored(player.uniqueId, e.player.uniqueId) }
    }

    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        if (e.action != Action.LEFT_CLICK_BLOCK && (e.getHand() == null || e.getHand() == EquipmentSlot.HAND)) {
            if (!cps.containsKey(e.player.uniqueId)) cps[e.player.uniqueId] = AtomicInteger()
            cps[e.player.uniqueId]!!.incrementAndGet()
            Bukkit.getScheduler().runTaskLater(this, {
                cps[e.player.uniqueId]!!.decrementAndGet()
            }, 20L)
        }
        if (e.action != Action.RIGHT_CLICK_AIR && e.action != Action.RIGHT_CLICK_BLOCK) return
        if (e.item == null || !e.item.isSimilar(PlayerCheckerCommand.ITEM)) return
        e.setUseItemInHand(Event.Result.DENY)
        e.setUseInteractedBlock(Event.Result.DENY)
        val location = e.player.location.clone()
        val entities = CollectionList(e.player.world.getNearbyEntities(location, 15.0, 15.0, 15.0))
        Thread {
            e.player.sendMessage(ChatColor.YELLOW.toString() + "===== Player Finder =====")
            entities.filter { entity: Entity? -> entity is Player }
                .map { entity: Entity -> entity as Player }
                .filter { player: Player -> e.player.uniqueId != player.uniqueId }
                .stream()
                .sorted { a: Player, b: Player -> (a.location.distance(location) - b.location.distance(location)).toInt() }
                .collect(Collectors.toCollection { CollectionList() })
                .forEach(Consumer { player: Player ->
                    val distance = (player.location.distance(location) * 100).roundToInt() / 100.0
                    e.player.sendMessage(ChatColor.GREEN.toString() + player.name + ChatColor.YELLOW + ": " + ChatColor.LIGHT_PURPLE + distance + ChatColor.YELLOW + "ブロック")
                })
            e.player.sendMessage(ChatColor.YELLOW.toString() + "============================")
        }.start()
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onEntityDamageByPlayer(e: EntityDamageByPlayerEvent) {
        if (!e.damager.inventory.itemInHand.isSimilar(PlayerCheckerCommand.ITEM)) return
        e.isCancelled = true
        if (e.entity.type != EntityType.PLAYER) return
        val player = e.entity as Player
        e.damager.sendMessage(ChatColor.YELLOW.toString() + "===== Player Finder: " + ChatColor.GREEN + player.name + ChatColor.YELLOW + " =====")
        e.damager.sendMessage(ChatColor.GREEN.toString() + " - Display Name: " + ChatColor.RESET + ChatColor.WHITE + player.displayName)
        e.damager.sendMessage(ChatColor.GREEN.toString() + " - UUID: " + ChatColor.RED + player.uniqueId.toString())
        e.damager.sendMessage(ChatColor.GREEN.toString() + " - Health: " + ChatColor.RED + (player.health * 100).roundToInt() / 100.0 + ChatColor.YELLOW + " / " + ChatColor.RED + player.maxHealth)
        e.damager.sendMessage(ChatColor.GREEN.toString() + " - Food Level: " + ChatColor.RED + player.foodLevel + ChatColor.YELLOW + " / " + ChatColor.RED + "20")
        e.damager.sendMessage(ChatColor.GREEN.toString() + " - Can Fly: " + ChatColor.RED + (if (player.allowFlight) "Yes" else "No"))
        player.activePotionEffects.forEach(Consumer { pe: PotionEffect ->
            val name = pe.type.name.toLowerCase().replace("_".toRegex(), " ")
            val duration = pe.duration / 20
            val level = pe.amplifier
            e.damager.sendMessage(ChatColor.GREEN.toString() + " - Potion Effect: " + ChatColor.LIGHT_PURPLE + name + ChatColor.RED + " x" + level + ChatColor.YELLOW + " (" + TomeitoAPI.secondsToTime(duration) + ")")
        })
        e.damager.sendMessage(ChatColor.YELLOW.toString() + "=================================")
    }

    companion object {
        @JvmField
        val opChat = CollectionSet<UUID>()
        @JvmField
        val modChat = CollectionSet<UUID>()
        @JvmField
        val commandLog = CollectionList<UUID>()
        @JvmField
        val PLAYERS = Collection<UUID, PlayerInfo>()
        @JvmField
        val REPORTS = CollectionList<ReportDetails>()
        @JvmField
        val warnQueue = Collection<UUID, String>()
        @JvmField
        val vanishedPlayers = CollectionSet<UUID?>()
        @JvmField
        var config: ConfigProvider? = null
        @JvmField
        var muteAll = false
        @JvmField
        var luckPerms: LuckPerms? = null
        @JvmStatic
        var instance: HackReport? = null
            private set

        @JvmStatic
        var connection: ConnectionHolder? = null

        @JvmStatic
        fun getPlayerInfo(name: String, uuid: UUID): PlayerInfo {
            if (!PLAYERS.containsKey(uuid)) PLAYERS.add(uuid, PlayerInfo(name, uuid))
            return PLAYERS[uuid]!!
        }

        fun sendMessage(e: PlayerCommandPreprocessEvent, message: String?) {
            val args = e.message.split("\\s+".toRegex()).toTypedArray()
            val list = CollectionList(*args)
            list.shift()
            list.shift()
            Webhook.sendWebhook(message, "理由: " + list.join(" "), Color.RED)
        }

        private val watchingPlayers = Collection<Player, Player>()

        @JvmStatic
        fun startWatching(player: Player, target: Player) {
            watchingPlayers[player] = target
        }

        @JvmStatic
        fun stopWatching(player: Player) = watchingPlayers.remove(player)

        @Suppress("DEPRECATION")
        @JvmStatic
        fun Player.sendActionbar(message: String?) {
            if (message == null) return
            var nmsVersion = Bukkit.getServer().javaClass.getPackage().name
            nmsVersion = nmsVersion.substring(nmsVersion.lastIndexOf(".") + 1)
            if (!nmsVersion.startsWith("v1_9_R") && !nmsVersion.startsWith("v1_8_R")) {
                Ref.getClass(Player.Spigot::class.java)
                    .getMethod("sendMessage", ChatMessageType::class.java, BaseComponent::class.java)
                    .invoke(this.spigot(), ChatMessageType.ACTION_BAR, TextComponent(message))
                return
            }
            try {
                val ppoc = ReflectionUtil.getNMSClass("PacketPlayOutChat")
                val chat = ReflectionUtil.getNMSClass(if (nmsVersion.equals("v1_8_R1", ignoreCase = true)) "ChatSerializer" else "ChatComponentText")
                val chatBaseComponent = ReflectionUtil.getNMSClass("IChatBaseComponent")
                var method: Method? = null
                if (nmsVersion.equals("v1_8_R1", ignoreCase = true)) method = chat.getDeclaredMethod("a", String::class.java)
                val c = if (nmsVersion.equals("v1_8_R1", ignoreCase = true)) chatBaseComponent.cast(Objects.requireNonNull(method)!!.invoke(chat, "{'text': '$message'}")) else chat.getConstructor(String::class.java).newInstance(message)
                val packetPlayOutChat = ppoc.getConstructor(chatBaseComponent, java.lang.Byte.TYPE).newInstance(c, 2.toByte())
                val playerConnection = ReflectionHelper.getFieldWithoutException(NMSAPI.getClassWithoutException("EntityPlayer"), CraftUtils.getHandle(this), "playerConnection")
                Ref.getClass(NMSAPI.getClassWithoutException("PlayerConnection")).getMethod("sendPacket", Packet.CLASS).invokeObj(playerConnection, packetPlayOutChat)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }
}
