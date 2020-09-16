package xyz.acrylicstyle.hackReport.struct

import java.util.UUID

data class Mute(val uuid: UUID, val name: String, val reason: String, val executor: UUID?, val timestamp: Long, val expiresAt: Long)
