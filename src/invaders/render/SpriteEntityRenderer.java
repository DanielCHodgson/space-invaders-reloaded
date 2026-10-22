package invaders.render;

import invaders.asset.SpriteId;
import invaders.asset.SpriteSource;
import invaders.entity.Entity;

import java.awt.Graphics2D;

/**
 * Source-image resolution is scaled to the entity's visual footprint, never its collision rules.
 */
public final class SpriteEntityRenderer<T extends Entity> implements EntityRenderer<T> {
    private final SpriteSource sprites;
    private final SpriteId sprite;

    public SpriteEntityRenderer(SpriteSource sprites, SpriteId sprite) {
        this.sprites = sprites;
        this.sprite = sprite;
    }

    @Override
    public void render(Graphics2D graphics, T entity, long ticks) {
        graphics.drawImage(
                sprites.frame(sprite, ticks),
                entity.x(),
                entity.y(),
                entity.width(),
                entity.height(),
                null);
    }
}
