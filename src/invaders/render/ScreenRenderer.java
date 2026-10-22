package invaders.render;

import java.awt.Graphics2D;

@FunctionalInterface
public interface ScreenRenderer {
    void render(Graphics2D graphics);
}
