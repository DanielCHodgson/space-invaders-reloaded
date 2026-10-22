package invaders.asset;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

/** Loads each image once and validates every frame rectangle before starting the game. */
public final class SpriteLibrary implements SpriteSource {
    private final BufferedImage background;
    private final Map<SpriteId, Animation> animations = new EnumMap<>(SpriteId.class);

    public SpriteLibrary(ResourceLoader resources, AssetCatalog catalog) throws IOException {
        Map<String, BufferedImage> images = new HashMap<>();
        background = load(resources, catalog.background());
        images.put(catalog.background(), background);
        for (SpriteId id : SpriteId.values()) {
            SpriteDefinition sprite = catalog.sprite(id);
            BufferedImage image = images.get(sprite.image());
            if (image == null) {
                image = load(resources, sprite.image());
                images.put(sprite.image(), image);
            }
            long right =
                    (long) sprite.x()
                            + (long) (sprite.frames() - 1) * sprite.stepX()
                            + sprite.frameWidth();
            long bottom =
                    (long) sprite.y()
                            + (long) (sprite.frames() - 1) * sprite.stepY()
                            + sprite.frameHeight();
            if (right > image.getWidth() || bottom > image.getHeight())
                throw new IOException(
                        "Sprite rectangles exceed image bounds: " + id + " in " + sprite.image());
            BufferedImage[] frames = new BufferedImage[sprite.frames()];
            for (int i = 0; i < frames.length; i++)
                frames[i] =
                        image.getSubimage(
                                sprite.x() + i * sprite.stepX(),
                                sprite.y() + i * sprite.stepY(),
                                sprite.frameWidth(),
                                sprite.frameHeight());
            animations.put(id, new Animation(frames, sprite.ticksPerFrame()));
        }
    }

    private static BufferedImage load(ResourceLoader resources, String path) throws IOException {
        try (InputStream stream = resources.open(path)) {
            BufferedImage image = ImageIO.read(stream);
            if (image == null) throw new IOException("Unsupported image: " + path);
            return image;
        }
    }

    @Override
    public BufferedImage background() {
        return background;
    }

    @Override
    public BufferedImage frame(SpriteId sprite, long ticks) {
        return animations.get(sprite).frame(ticks);
    }
}
