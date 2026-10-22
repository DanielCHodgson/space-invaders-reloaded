package invaders.asset;

import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/**
 * Logical asset names and sprite-sheet metadata live in res/assets.properties, not gameplay code.
 */
public final class AssetCatalog {
    private final String background;
    private final Map<SpriteId, SpriteDefinition> sprites;
    private final Map<SoundId, String> sounds;

    private AssetCatalog(Properties properties) {
        background = required(properties, "background");
        EnumMap<SpriteId, SpriteDefinition> definitions = new EnumMap<>(SpriteId.class);
        for (SpriteId id : SpriteId.values()) {
            String key = "sprite." + id.name().toLowerCase(Locale.ROOT) + ".";
            definitions.put(
                    id,
                    new SpriteDefinition(
                            required(properties, key + "image"),
                            number(properties, key + "x"),
                            number(properties, key + "y"),
                            number(properties, key + "frameWidth"),
                            number(properties, key + "frameHeight"),
                            number(properties, key + "frames"),
                            number(properties, key + "stepX"),
                            number(properties, key + "stepY"),
                            number(properties, key + "ticksPerFrame")));
        }
        sprites = Map.copyOf(definitions);
        EnumMap<SoundId, String> soundFiles = new EnumMap<>(SoundId.class);
        for (SoundId id : SoundId.values())
            soundFiles.put(id, required(properties, "sound." + id.name().toLowerCase(Locale.ROOT)));
        sounds = Map.copyOf(soundFiles);
    }

    public static AssetCatalog load(ResourceLoader resources) throws IOException {
        Properties properties = new Properties();
        try (InputStream stream = resources.open("assets.properties")) {
            properties.load(stream);
        }
        try {
            return new AssetCatalog(properties);
        } catch (IllegalArgumentException e) {
            throw new IOException("Invalid assets.properties: " + e.getMessage(), e);
        }
    }

    private static String required(Properties values, String key) {
        String value = values.getProperty(key);
        if (value == null || value.isBlank()) throw new IllegalArgumentException("Missing " + key);
        return value.trim();
    }

    private static int number(Properties values, String key) {
        return Integer.parseInt(required(values, key));
    }

    public String background() {
        return background;
    }

    public SpriteDefinition sprite(SpriteId id) {
        return sprites.get(id);
    }

    public String sound(SoundId id) {
        return sounds.get(id);
    }
}
