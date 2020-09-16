// https://gist.github.com/k3kdude/fba6f6b37594eae3d6f9475330733bdb
package xyz.acrylicstyle.hackReport.utils

import xyz.acrylicstyle.hackReport.utils.Utils.webhook
import xyz.acrylicstyle.tomeito_api.utils.Log
import java.awt.Color
import java.io.IOException
import java.lang.reflect.Array
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.ArrayList
import java.util.HashMap
import javax.net.ssl.HttpsURLConnection

class Webhook(private val url: String) {
    private val content: String? = null
    var username: String? = null
    private val avatarUrl: String? = null
    private val tts = false
    private val embeds: MutableList<EmbedObject> = ArrayList()
    fun addEmbed(embed: EmbedObject) {
        embeds.add(embed)
    }

    @Throws(IOException::class)
    fun execute() {
        if (content == null && embeds.isEmpty()) throw IllegalArgumentException("Set content or add at least one EmbedObject")
        val json = JSONObject()
        json.put("content", content)
        json.put("username", username)
        json.put("avatar_url", avatarUrl)
        json.put("tts", tts)
        if (embeds.isNotEmpty()) {
            val embedObjects: MutableList<JSONObject> = ArrayList()
            for (embed: EmbedObject in embeds) {
                val jsonEmbed = JSONObject()
                jsonEmbed.put("title", embed.title)
                jsonEmbed.put("description", embed.description)
                jsonEmbed.put("url", embed.url)
                if (embed.color != null) {
                    val color = embed.color!!
                    var rgb = color.red
                    rgb = (rgb shl 8) + color.green
                    rgb = (rgb shl 8) + color.blue
                    jsonEmbed.put("color", rgb)
                }
                val footer = embed.footer
                val image = embed.image
                val thumbnail = embed.thumbnail
                val author = embed.author
                val fields = embed.fields
                if (footer != null) {
                    val jsonFooter = JSONObject()
                    jsonFooter.put("text", footer.text)
                    jsonFooter.put("icon_url", footer.iconUrl)
                    jsonEmbed.put("footer", jsonFooter)
                }
                if (image != null) {
                    val jsonImage = JSONObject()
                    jsonImage.put("url", image.url)
                    jsonEmbed.put("image", jsonImage)
                }
                if (thumbnail != null) {
                    val jsonThumbnail = JSONObject()
                    jsonThumbnail.put("url", thumbnail.url)
                    jsonEmbed.put("thumbnail", jsonThumbnail)
                }
                if (author != null) {
                    val jsonAuthor = JSONObject()
                    jsonAuthor.put("name", author.name)
                    jsonAuthor.put("url", author.url)
                    jsonAuthor.put("icon_url", author.iconUrl)
                    jsonEmbed.put("author", jsonAuthor)
                }
                val jsonFields: MutableList<JSONObject> = ArrayList()
                for (field: EmbedObject.Field in fields) {
                    val jsonField = JSONObject()
                    jsonField.put("name", field.name)
                    jsonField.put("value", field.value)
                    jsonField.put("inline", field.inline)
                    jsonFields.add(jsonField)
                }
                jsonEmbed.put("fields", jsonFields.toTypedArray())
                embedObjects.add(jsonEmbed)
            }
            json.put("embeds", embedObjects.toTypedArray())
        }
        val url = URL(url)
        val connection = url.openConnection() as HttpsURLConnection
        connection.addRequestProperty("Accept", "application/json")
        connection.addRequestProperty("Content-Type", "application/json")
        connection.addRequestProperty("User-Agent", "Spigot HackReport Plugin")
        connection.doOutput = true
        connection.requestMethod = "POST"
        val stream = connection.outputStream
        val jsons = json.toString()
        Log.info("JSON: $jsons")
        stream.write(jsons.toByteArray(StandardCharsets.UTF_8))
        stream.flush()
        stream.close()
        connection.inputStream.close()
        connection.disconnect()
    }

    class EmbedObject {
        var title: String? = null

        var description: String? = null

        val url: String? = null

        var color: Color? = null

        val footer: Footer? = null

        val thumbnail: Thumbnail? = null

        val image: Image? = null

        var author: Author? = null

        val fields: MutableList<Field> = ArrayList()

        fun setAuthor(name: String?, url: String?, icon: String?): EmbedObject {
            author = Author(name, url, icon)
            return this
        }

        fun addField(name: String?, value: String?, inline: Boolean): EmbedObject {
            fields.add(Field(name, value, inline))
            return this
        }

        class Footer(val text: String? = null, val iconUrl: String? = null)

        class Thumbnail(val url: String? = null)

        class Image(val url: String? = null)

        class Author(val name: String? = null, val url: String? = null, val iconUrl: String? = null)

        class Field(val name: String? = null, val value: String? = null, val inline: Boolean = false)
    }

    private class JSONObject {
        private val map = HashMap<String, Any>()
        fun put(key: String, value: Any?) {
            if (value != null) {
                map[key] = value
            }
        }

        override fun toString(): String {
            val builder = StringBuilder()
            val entrySet: Set<Map.Entry<String, Any>> = map.entries
            builder.append("{")
            var i = 0
            for ((key, `val`) in entrySet) {
                builder.append(quote(key)).append(":")
                if (`val` is String) {
                    builder.append(quote(`val`.toString()))
                } else if (`val` is Int) {
                    builder.append(Integer.valueOf(`val`.toString()))
                } else if (`val` is Boolean) {
                    builder.append(`val`)
                } else if (`val` is JSONObject) {
                    builder.append(`val`.toString())
                } else if (`val`.javaClass.isArray) {
                    builder.append("[")
                    val len = Array.getLength(`val`)
                    for (j in 0 until len) {
                        builder.append(Array.get(`val`, j).toString()).append(if (j != len - 1) "," else "")
                    }
                    builder.append("]")
                }
                builder.append(if (++i == entrySet.size) "}" else ",")
            }
            return builder.toString()
        }

        private fun quote(string: String): String {
            return "\"" + string.replace("\"".toRegex(), "'") + "\""
        }
    }

    companion object {
        fun sendWebhook(title: String?, description: String?, color: Color?) {
            val webhook = webhook ?: return
            Thread {
                webhook.addEmbed(
                    EmbedObject()
                        .apply { this.title = title }
                        .apply { this.color = color }
                        .apply { this.description = description }
                )
                try {
                    webhook.execute()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }.start()
        }
    }
}