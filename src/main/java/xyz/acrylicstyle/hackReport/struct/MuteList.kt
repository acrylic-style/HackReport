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

class MuteList(private val t: Table) {
    companion object {
        private val cache = Collection<UUID, DataCache<Boolean>>()
    }

    fun contains(element: UUID): Boolean {
        cache[element]?.let {
            val bool = it.get()
            if (bool != null) return bool
        }
        return t.findOne(FindOptions.Builder().addWhere("uuid", element.toString()).build()).then {
            val bool = it != null
            cache.add(element, DataCache(bool, System.currentTimeMillis() + 1000 * 60 * 10)) // 10 minutes
            return@then bool
        }.complete()
    }

    fun get(uuid: UUID): Promise<Mute?> = t.findOne(FindOptions.Builder().addWhere("uuid", uuid.toString()).build()).then {
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

    fun add(mute: Mute) {
        cache.remove(mute.uuid)
        t.insert(
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

    fun clear() {
        cache.clear()
        t.delete(FindOptions.Builder().addWhere("true", true).build()).queue()
    }

    fun remove(element: UUID): Boolean {
        cache.remove(element)
        t.delete(FindOptions.Builder().addWhere("uuid", "\"${element}\"").build()).queue()
        return true
    }

    fun toMap(): Collection<UUID, String> {
        val map: Collection<UUID, String> = Collection()
        this.forEach {
            val uuid = UUID.fromString(it.getString("uuid"))
            val name = it.getString("name")
            map.add(uuid, name)
        }
        return map
    }

    fun filter(filter: Function<UUID, Boolean>): Collection<UUID, String> {
        val map: Collection<UUID, String> = Collection()
        this.forEach {
            val uuid = UUID.fromString(it.getString("uuid"))
            val name = it.getString("name")
            if (filter.apply(uuid)) {
                map.add(uuid, name)
            }
        }
        return map
    }

    fun forEach(action: Consumer<in TableData>) {
        t.findAll(null).complete().forEach { action.accept(it) }
    }
}
