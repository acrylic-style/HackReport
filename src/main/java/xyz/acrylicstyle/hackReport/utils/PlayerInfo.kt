package xyz.acrylicstyle.hackReport.utils

import java.util.UUID

class PlayerInfo(val name: String, val uniqueId: UUID) {
    var reports = 0
    var kills = 0
        private set
    var deaths = 0
        private set

    fun increaseReports() {
        reports++
    }

    fun increaseKills() {
        kills++
    }

    fun increaseDeaths() {
        deaths++
    }
}