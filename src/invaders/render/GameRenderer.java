package invaders.render;

import invaders.asset.SpriteSource;
import invaders.config.GameRules;
import invaders.scene.SceneId;
import invaders.scene.SceneView;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.EnumMap;
import java.util.Map;

/** Renders immutable snapshots and scene views, with no access to gameplay mutation. */
public final class GameRenderer {
    private final SpriteSource sprites;

    private final SceneView scenes;
    private final Map<SceneId, ScreenRenderer> screens = new EnumMap<>(SceneId.class);

    public GameRenderer(SpriteSource sprites, SceneView scenes) {
        this.sprites = sprites;

        this.scenes = scenes;
    }

    public void register(SceneId id, ScreenRenderer renderer) {
        if (screens.putIfAbsent(id, renderer) != null)
            throw new IllegalArgumentException("Renderer already registered: " + id);
    }

    public void render(Graphics2D g, int width, int height) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);
        Viewport viewport = Viewport.fit(width, height);
        g.translate(viewport.offsetX(), viewport.offsetY());
        g.scale(viewport.scale(), viewport.scale());
        g.clipRect(0, 0, GameRules.WIDTH, GameRules.HEIGHT);
        g.drawImage(sprites.background(), 0, 0, GameRules.WIDTH, GameRules.HEIGHT, null);
        g.setColor(Color.WHITE);
        ScreenRenderer renderer = screens.get(scenes.currentId());
        if (renderer == null)
            throw new IllegalStateException("No renderer for scene " + scenes.currentId());
        renderer.render(g);
    }
}
