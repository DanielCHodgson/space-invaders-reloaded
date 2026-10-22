package invaders.tests;

import static invaders.tests.TestSupport.*;

import invaders.asset.*;
import invaders.entity.Player;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;

public final class ResourceTests {
    private ResourceTests() {}

    public static void run() throws Exception {
        ResourceLoader source = new ClasspathResourceLoader();
        test(
                "original sprite manifest loads every animation",
                () -> {
                    SpriteLibrary sprites = new SpriteLibrary(source, AssetCatalog.load(source));
                    check(sprites.frame(SpriteId.PLAYER, 0).getWidth() == 32, "Player frame");
                    check(sprites.frame(SpriteId.MOTHERSHIP, 0).getWidth() == 64, "Boss frame");
                    check(
                            sprites.frame(SpriteId.LASER, 0).getWidth() == 8,
                            "Laser source rectangle");
                    check(
                            sprites.frame(SpriteId.ALIEN, 0) == sprites.frame(SpriteId.ALIEN, 88),
                            "Cached animation wraps");
                });
        test(
                "shared sprite sheet is loaded only once",
                () -> {
                    Map<String, Integer> counts = new HashMap<>();
                    ResourceLoader counted =
                            name -> {
                                counts.merge(name, 1, Integer::sum);
                                return source.open(name);
                            };
                    new SpriteLibrary(counted, AssetCatalog.load(counted));
                    check(counts.get("sprite_sheet.png") == 1, "Single shared image decode");
                });
        test(
                "double-resolution artwork slots in without changing entity dimensions",
                () -> {
                    Properties config = properties(source);
                    for (SpriteId id : SpriteId.values()) {
                        String prefix =
                                "sprite." + id.name().toLowerCase(java.util.Locale.ROOT) + ".";
                        for (String field :
                                new String[] {
                                    "x", "y", "frameWidth", "frameHeight", "stepX", "stepY"
                                })
                            config.setProperty(
                                    prefix + field,
                                    Integer.toString(
                                            Integer.parseInt(config.getProperty(prefix + field))
                                                    * 2));
                    }
                    BufferedImage original;
                    try (InputStream in = source.open("sprite_sheet.png")) {
                        original = ImageIO.read(in);
                    }
                    BufferedImage large =
                            new BufferedImage(
                                    original.getWidth() * 2,
                                    original.getHeight() * 2,
                                    BufferedImage.TYPE_INT_ARGB);
                    var graphics = large.createGraphics();
                    graphics.drawImage(original, 0, 0, large.getWidth(), large.getHeight(), null);
                    graphics.dispose();
                    ByteArrayOutputStream png = new ByteArrayOutputStream();
                    ImageIO.write(large, "png", png);
                    byte[] metadata = encode(config), image = png.toByteArray();
                    ResourceLoader replacement =
                            name ->
                                    switch (name) {
                                        case "assets.properties" ->
                                                new ByteArrayInputStream(metadata);
                                        case "sprite_sheet.png" -> new ByteArrayInputStream(image);
                                        default -> source.open(name);
                                    };
                    SpriteLibrary swapped =
                            new SpriteLibrary(replacement, AssetCatalog.load(replacement));
                    check(
                            swapped.frame(SpriteId.PLAYER, 0).getWidth() == 64
                                    && Player.initial().width() == 32,
                            "Artwork and collision sizes independent");
                });
        test(
                "invalid sprite rectangles fail during loading",
                () -> {
                    Properties config = properties(source);
                    config.setProperty("sprite.player.x", "5000");
                    byte[] metadata = encode(config);
                    ResourceLoader bad =
                            name ->
                                    name.equals("assets.properties")
                                            ? new ByteArrayInputStream(metadata)
                                            : source.open(name);
                    try {
                        new SpriteLibrary(bad, AssetCatalog.load(bad));
                        throw new AssertionError("Invalid frame accepted");
                    } catch (IOException expected) {
                        check(expected.getMessage().contains("exceed"), "Useful bounds error");
                    }
                });
        test(
                "missing asset metadata fails with its key",
                () -> {
                    ResourceLoader bad = name -> new ByteArrayInputStream(new byte[0]);
                    try {
                        AssetCatalog.load(bad);
                        throw new AssertionError("Missing metadata accepted");
                    } catch (IOException expected) {
                        check(
                                expected.getMessage().contains("background"),
                                "Missing key identified");
                    }
                });
        test(
                "missing resources produce a useful exception",
                () -> {
                    try {
                        source.open("not-a-real-resource.png");
                        throw new AssertionError("Missing resource accepted");
                    } catch (IOException expected) {
                        check(
                                expected.getMessage().contains("Missing resource"),
                                "Useful missing-file error");
                    }
                });
    }

    private static Properties properties(ResourceLoader source) throws IOException {
        Properties values = new Properties();
        try (InputStream in = source.open("assets.properties")) {
            values.load(in);
        }
        return values;
    }

    private static byte[] encode(Properties properties) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        properties.store(out, null);
        return out.toByteArray();
    }
}
