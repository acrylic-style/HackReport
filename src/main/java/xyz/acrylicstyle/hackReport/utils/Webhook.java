// https://gist.github.com/k3kdude/fba6f6b37594eae3d6f9475330733bdb

package xyz.acrylicstyle.hackReport.utils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import xyz.acrylicstyle.tomeito_api.utils.Log;

import javax.net.ssl.HttpsURLConnection;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class used to execute Discord Webhooks with low effort
 */
@SuppressWarnings("unused")
public class Webhook {
    @Getter private final String url;
    @Getter @Setter private String content;
    @Getter @Setter private String username;
    @Getter @Setter private String avatarUrl;
    @Getter @Setter private boolean tts;
    private final List<EmbedObject> embeds = new ArrayList<>();

    /**
     * Constructs a new DiscordWebhook instance
     *
     * @param url The webhook URL obtained in Discord
     */
    public Webhook(@NonNull String url) { this.url = url; }

    public void addEmbed(@NonNull EmbedObject embed) { this.embeds.add(embed); }

    public void execute() throws IOException {
        if (this.content == null && this.embeds.isEmpty()) throw new IllegalArgumentException("Set content or add at least one EmbedObject");
        JSONObject json = new JSONObject();
        json.put("content", this.content);
        json.put("username", this.username);
        json.put("avatar_url", this.avatarUrl);
        json.put("tts", this.tts);
        if (!this.embeds.isEmpty()) {
            List<JSONObject> embedObjects = new ArrayList<>();
            for (EmbedObject embed : this.embeds) {
                JSONObject jsonEmbed = new JSONObject();
                jsonEmbed.put("title", embed.getTitle());
                jsonEmbed.put("description", embed.getDescription());
                jsonEmbed.put("url", embed.getUrl());
                if (embed.getColor() != null) {
                    Color color = embed.getColor();
                    int rgb = color.getRed();
                    rgb = (rgb << 8) + color.getGreen();
                    rgb = (rgb << 8) + color.getBlue();
                    jsonEmbed.put("color", rgb);
                }
                EmbedObject.Footer footer = embed.getFooter();
                EmbedObject.Image image = embed.getImage();
                EmbedObject.Thumbnail thumbnail = embed.getThumbnail();
                EmbedObject.Author author = embed.getAuthor();
                List<EmbedObject.Field> fields = embed.getFields();
                if (footer != null) {
                    JSONObject jsonFooter = new JSONObject();
                    jsonFooter.put("text", footer.getText());
                    jsonFooter.put("icon_url", footer.getIconUrl());
                    jsonEmbed.put("footer", jsonFooter);
                }
                if (image != null) {
                    JSONObject jsonImage = new JSONObject();
                    jsonImage.put("url", image.getUrl());
                    jsonEmbed.put("image", jsonImage);
                }
                if (thumbnail != null) {
                    JSONObject jsonThumbnail = new JSONObject();
                    jsonThumbnail.put("url", thumbnail.getUrl());
                    jsonEmbed.put("thumbnail", jsonThumbnail);
                }
                if (author != null) {
                    JSONObject jsonAuthor = new JSONObject();
                    jsonAuthor.put("name", author.getName());
                    jsonAuthor.put("url", author.getUrl());
                    jsonAuthor.put("icon_url", author.getIconUrl());
                    jsonEmbed.put("author", jsonAuthor);
                }
                List<JSONObject> jsonFields = new ArrayList<>();
                for (EmbedObject.Field field : fields) {
                    JSONObject jsonField = new JSONObject();
                    jsonField.put("name", field.getName());
                    jsonField.put("value", field.getValue());
                    jsonField.put("inline", field.isInline());
                    jsonFields.add(jsonField);
                }
                jsonEmbed.put("fields", jsonFields.toArray());
                embedObjects.add(jsonEmbed);
            }
            json.put("embeds", embedObjects.toArray());
        }
        URL url = new URL(this.url);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.addRequestProperty("Accept", "application/json");
        connection.addRequestProperty("Content-Type", "application/json");
        connection.addRequestProperty("User-Agent", "Spigot HackReport Plugin");
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        OutputStream stream = connection.getOutputStream();
        String jsons = json.toString();
        Log.info("JSON: " + jsons);
        stream.write(jsons.getBytes(StandardCharsets.UTF_8));
        stream.flush();
        stream.close();
        connection.getInputStream().close();
        connection.disconnect();
    }

    public static class EmbedObject {
        @Getter @Setter @Accessors(chain = true) private String title;
        @Getter @Setter @Accessors(chain = true) private String description;
        @Getter @Setter @Accessors(chain = true) private String url;
        @Getter @Setter @Accessors(chain = true) private Color color;
        @Getter @Setter @Accessors(chain = true) private Footer footer;
        @Getter @Setter @Accessors(chain = true) private Thumbnail thumbnail;
        @Getter @Setter @Accessors(chain = true) private Image image;
        @Getter private Author author;
        @Getter private final List<Field> fields = new ArrayList<>();

        public EmbedObject setAuthor(String name, String url, String icon) {
            this.author = new Author(name, url, icon);
            return this;
        }

        public EmbedObject addField(String name, String value, boolean inline) {
            this.fields.add(new Field(name, value, inline));
            return this;
        }

        @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
        private static class Footer {
            @Getter(AccessLevel.PRIVATE) private final String text;
            @Getter(AccessLevel.PRIVATE) private final String iconUrl;
        }

        @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
        private static class Thumbnail {
            @Getter(AccessLevel.PRIVATE) private final String url;
        }

        @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
        private static class Image {
            @Getter(AccessLevel.PRIVATE) private final String url;
        }

        @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
        private static class Author {
            @Getter(AccessLevel.PRIVATE) private final String name;
            @Getter(AccessLevel.PRIVATE) private final String url;
            @Getter(AccessLevel.PRIVATE) private final String iconUrl;
        }

        @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
        private static class Field {
            @Getter(AccessLevel.PRIVATE) private final String name;
            @Getter(AccessLevel.PRIVATE) private final String value;
            @Getter(AccessLevel.PRIVATE) private final boolean inline;
        }
    }

    private static class JSONObject {
        private final HashMap<String, Object> map = new HashMap<>();

        void put(String key, Object value) {
            if (value != null) {
                map.put(key, value);
            }
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            Set<Map.Entry<String, Object>> entrySet = map.entrySet();
            builder.append("{");

            int i = 0;
            for (Map.Entry<String, Object> entry : entrySet) {
                Object val = entry.getValue();
                builder.append(quote(entry.getKey())).append(":");
                if (val instanceof String) {
                    builder.append(quote(String.valueOf(val)));
                } else if (val instanceof Integer) {
                    builder.append(Integer.valueOf(String.valueOf(val)));
                } else if (val instanceof Boolean) {
                    builder.append(val);
                } else if (val instanceof JSONObject) {
                    builder.append(val.toString());
                } else if (val.getClass().isArray()) {
                    builder.append("[");
                    int len = Array.getLength(val);
                    for (int j = 0; j < len; j++) {
                        builder.append(Array.get(val, j).toString()).append(j != len - 1 ? "," : "");
                    }
                    builder.append("]");
                }
                builder.append(++i == entrySet.size() ? "}" : ",");
            }
            return builder.toString();
        }

        private String quote(String string) {
            return "\"" + string.replaceAll("\"", "'") + "\"";
        }
    }
}
