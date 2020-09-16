package xyz.acrylicstyle.hackReport.utils

import xyz.acrylicstyle.hackReport.struct.MuteList
import xyz.acrylicstyle.sql.DataType
import xyz.acrylicstyle.sql.Sequelize
import xyz.acrylicstyle.sql.TableDefinition
import java.sql.Driver
import java.util.Properties

class ConnectionHolder(host: String, name: String, username: String, password: String) : Sequelize(host, name, username, password) {
    companion object {
        lateinit var muteList: MuteList
        var ready = false
    }

    init {
        var driver: Driver? = null
        try {
            driver = Class.forName("com.mysql.cj.jdbc.Driver").newInstance() as Driver
        } catch (ignore: ReflectiveOperationException) {}
        if (driver == null) {
            try {
                driver = Class.forName("com.mysql.jdbc.Driver").newInstance() as Driver
            } catch (ignore: ReflectiveOperationException) {}
        }
        if (driver == null) throw NoSuchElementException("Could not find any MySQL driver")
        val prop = Properties()
        prop.setProperty("maxReconnects", "3")
        prop.setProperty("autoReconnect", "true")
        authenticate(driver, prop)
        muteList = MuteList(
            define("mute", arrayOf(
                TableDefinition.Builder("uuid", DataType.STRING).setPrimaryKey(true).setAllowNull(false).build(),
                TableDefinition.Builder("name", DataType.STRING).setAllowNull(false).build(),
                TableDefinition.Builder("reason", DataType.STRING).setAllowNull(false).build(),
                TableDefinition.Builder("executor", DataType.STRING).build(),
                TableDefinition.Builder("expiresAt", DataType.BIGINT).build(),
                TableDefinition.Builder("timestamp", DataType.BIGINT).build(),
            ))
        )
        sync()
        ready = true
    }
}
