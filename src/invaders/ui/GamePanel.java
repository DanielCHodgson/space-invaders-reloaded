package invaders.ui;

import invaders.config.GameRules;
import invaders.render.GameRenderer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

/** Swing painting adapter only. Owns no game rules, inputs, sounds or timer. */
@SuppressWarnings("serial")
public final class GamePanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final GameRenderer renderer;

    public GamePanel(GameRenderer renderer) {
        this.renderer = renderer;
        setPreferredSize(new Dimension(GameRules.WIDTH, GameRules.HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D copy = (Graphics2D) graphics.create();
        try {
            renderer.render(copy, getWidth(), getHeight());
        } finally {
            copy.dispose();
        }
    }
}
