package invaders.render;

import invaders.scene.SceneId;

import java.awt.Graphics2D;

public final class HelpRenderer implements ScreenRenderer {
    private final MenuPainter painter;

    public HelpRenderer(MenuPainter painter) {
        this.painter = painter;
    }

    @Override
    public void render(Graphics2D g) {
        g.setFont(MenuPainter.TITLE);
        painter.centered(g, "HELP", 100);
        g.setFont(MenuPainter.SMALL);
        painter.centered(g, "A / D or left / right arrows to move", 165);
        painter.centered(g, "Space to shoot. Release to fire again.", 200);
        painter.centered(g, "Stop the aliens reaching your ship.", 235);
        painter.buttons(g, SceneId.HELP);
    }
}
