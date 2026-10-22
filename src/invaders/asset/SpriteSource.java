package invaders.asset;

import java.awt.image.BufferedImage;

public interface SpriteSource {
    BufferedImage background();

    BufferedImage frame(SpriteId sprite, long ticks);
}
