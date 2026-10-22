package invaders.render;

import invaders.world.GameSnapshot;
import invaders.world.GameView;

import java.awt.Graphics2D;

public final class GameplayRenderer implements ScreenRenderer {
    private final EntityRendererRegistry entities;
    private final GameView game;

    public GameplayRenderer(EntityRendererRegistry entities, GameView game) {
        this.entities = entities;
        this.game = game;
    }

    @Override
    public void render(Graphics2D g) {
        GameSnapshot snapshot = game.snapshot();
        snapshot.enemies().forEach(e -> entities.render(g, e, snapshot.ticks()));
        snapshot.lasers().forEach(e -> entities.render(g, e, snapshot.ticks()));
        entities.render(g, snapshot.player(), snapshot.ticks());
        g.setFont(MenuPainter.SMALL);
        g.drawString("Score: " + snapshot.score(), 25, 25);
    }
}
