package xyz.acrylicstyle.hackReport.utils

import util.CollectionList
import java.util.UUID

class ReportDetails(val name: String, val uniqueId: UUID, description: List<String>?) {
    val description: CollectionList<String>

    init {
        this.description = CollectionList(description)
    }
}