package xyz.acrylicstyle.hackReport.utils

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.UUID

class HackReportPlayer(val uniqueId: UUID) {
    val player: Player? = Bukkit.getPlayer(uniqueId)
    val isOnline: Boolean
        get() = player != null && player.isOnline
    val health: Double
        get() = player?.health ?: 0.0
    val foodLevel: Int
        get() = player?.foodLevel ?: 0
    val isFlying: Boolean
        get() = player != null && player.isFlying
    val allowFlight: Boolean
        get() = player != null && player.allowFlight

}
