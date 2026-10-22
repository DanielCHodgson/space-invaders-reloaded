package invaders.render;

import invaders.scene.SceneId;

import java.awt.Graphics2D;

public final class MenuRenderer implements ScreenRenderer {
    private final SceneId scene;
    private final String title;
    private final MenuPainter painter;

    public MenuRenderer(SceneId scene, String title, MenuPainter painter) {
        this.scene = scene;
        this.title = title;
        this.painter = painter;
    }

    @Override
    public void render(Graphics2D g) {
        g.setFont(MenuPainter.TITLE);
        painter.centered(g, title, 100);
        painter.buttons(g, scene);
    }
}
