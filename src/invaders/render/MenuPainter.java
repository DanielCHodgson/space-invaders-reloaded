package invaders.render;

import invaders.config.GameRules;
import invaders.scene.SceneId;
import invaders.scene.menu.MenuButton;
import invaders.scene.menu.MenuLayout;

import java.awt.Font;
import java.awt.Graphics2D;

/** Shared text/button primitives; MenuLayout supplies all interactive geometry. */
public final class MenuPainter {
    public static final Font TITLE = new Font("Arial", Font.BOLD, 50);
    public static final Font SMALL = new Font("Arial", Font.BOLD, 15);
    private static final Font BUTTON = new Font("Arial", Font.BOLD, 30);
    private final MenuLayout layout;

    public MenuPainter(MenuLayout layout) {
        this.layout = layout;
    }

    public void centered(Graphics2D g, String text, int baseline) {
        g.drawString(text, (GameRules.WIDTH - g.getFontMetrics().stringWidth(text)) / 2, baseline);
    }

    public void buttons(Graphics2D g, SceneId scene) {
        g.setFont(BUTTON);
        for (MenuButton button : layout.buttons(scene)) {
            g.drawRect(button.x(), button.y(), button.width(), button.height());
            int x =
                    button.x()
                            + (button.width() - g.getFontMetrics().stringWidth(button.label())) / 2;
            g.drawString(button.label(), x, button.y() + 35);
        }
    }
}
