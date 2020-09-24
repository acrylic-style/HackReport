package xyz.acrylicstyle.hackReport.struct

import util.Collection
import util.promise.Promise
import util.ref.DataCache
import xyz.acrylicstyle.sql.Table
import xyz.acrylicstyle.sql.TableData
import xyz.acrylicstyle.sql.options.FindOptions
import xyz.acrylicstyle.sql.options.InsertOptions
import java.util.UUID
import java.util.function.Consumer
import java.util.function.Function

class MuteList(private val t: Table, private val t2: Table) {
    companion object {
        private val cache = Collection<UUID, DataCache<Boolean>>()
        private val tellCache = Collection<UUID, DataCache<Boolean>>()
    }

    fun contains(element: UUID, tell: Boolean): Boolean {
        (if (tell) tellCache else cache)[element]?.let {
            val bool = it.get()
            if (bool != null) return bool
        }
        return (if (tell) t2 else t).findOne(FindOptions.Builder().addWhere("uuid", element.toString()).build()).then {
            val bool = it != null
            (if (tell) tellCache else cache).add(element, DataCache(bool, System.currentTimeMillis() + 1000 * 60 * 10)) // 10 minutes
            return@then bool
        }.complete()
    }

    fun get(uuid: UUID, tell: Boolean): Promise<Mute?> = (if (tell) t2 else t).findOne(FindOptions.Builder().addWhere("uuid", uuid.toString()).build()).then {
        if (it == null) return@then null
        val executor = it.getString("executor")
        return@then Mute(
            uuid,
            it.getString("name"),
            it.getString("reason"),
            if (executor == null) null else UUID.fromString(executor),
            it.getLong("timestamp"),
            it.getLong("expiresAt"),
        )
    }

    fun getName(uuid: UUID): Promise<String?> = t.findOne(FindOptions.Builder().addWhere("uuid", uuid.toString()).build()).then {
        if (it == null) return@then null
        return@then it.getString("name")
    }

    fun add(mute: Mute, tell: Boolean) {
        (if (tell) tellCache else cache).remove(mute.uuid)
        (if (tell) t2 else t).insert(
            InsertOptions.Builder()
                .addValue("uuid", mute.uuid.toString())
                .addValue("name", mute.name)
                .addValue("reason", mute.reason)
                .addValue("executor", mute.executor?.toString())
                .addValue("timestamp", mute.timestamp)
                .addValue("expiresAt", mute.expiresAt)
                .build()
        ).queue()
    }

    fun clearCache() {
        tellCache.clear()
        cache.clear()
    }

    fun remove(element: UUID, tell: Boolean): Boolean {
        (if (tell) tellCache else cache).remove(element)
        (if (tell) t2 else t).delete(FindOptions.Builder().addWhere("uuid", element.toString()).build()).queue()
        return true
    }

    fun toMap(tell: Boolean): Collection<UUID, String> {
        val map: Collection<UUID, String> = Collection()
        this.forEach(tell) {
            val uuid = UUID.fromString(it.getString("uuid"))
            val name = it.getString("name")
            map.add(uuid, name)
        }
        return map
    }

    fun filter(filter: Function<UUID, Boolean>, tell: Boolean): Collection<UUID, String> {
        val map: Collection<UUID, String> = Collection()
        this.forEach(tell) {
            val uuid = UUID.fromString(it.getString("uuid"))
            val name = it.getString("name")
            if (filter.apply(uuid)) {
                map.add(uuid, name)
            }
        }
        return map
    }

    fun forEach(tell: Boolean, action: Consumer<in TableData>) {
        (if (tell) t2 else t).findAll(null).complete().forEach { action.accept(it) }
    }
}
