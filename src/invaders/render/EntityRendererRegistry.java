package invaders.render;

import invaders.entity.Entity;

import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

/**
 * New entity types supply a renderer at composition time rather than editing a rendering switch.
 */
public final class EntityRendererRegistry {
    private final Map<Class<?>, EntityRenderer<Entity>> renderers = new HashMap<>();

    public <T extends Entity> void register(Class<T> type, EntityRenderer<? super T> renderer) {
        if (renderers.putIfAbsent(
                        type,
                        (graphics, entity, ticks) ->
                                renderer.render(graphics, type.cast(entity), ticks))
                != null)
            throw new IllegalArgumentException(
                    "Renderer already registered: " + type.getSimpleName());
    }

    public void render(Graphics2D graphics, Entity entity, long ticks) {
        EntityRenderer<Entity> renderer = renderers.get(entity.getClass());
        if (renderer == null)
            throw new IllegalArgumentException("No renderer for " + entity.getClass().getName());
        renderer.render(graphics, entity, ticks);
    }
}
