package invaders.render;

import invaders.entity.Entity;

import java.awt.Graphics2D;

@FunctionalInterface
public interface EntityRenderer<T extends Entity> {
    void render(Graphics2D graphics, T entity, long ticks);
}
