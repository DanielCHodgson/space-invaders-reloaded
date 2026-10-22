package invaders.render;

import invaders.config.GameRules;

import java.awt.Point;
import java.util.Optional;

/** Keeps presentation scaling and pointer coordinates on the same logical playfield. */
public record Viewport(double scale, double offsetX, double offsetY) {
    public static Viewport fit(int width, int height) {
        double scale =
                Math.max(
                        0.001,
                        Math.min(
                                (double) width / GameRules.WIDTH,
                                (double) height / GameRules.HEIGHT));
        return new Viewport(
                scale,
                (width - GameRules.WIDTH * scale) / 2,
                (height - GameRules.HEIGHT * scale) / 2);
    }

    public Optional<Point> toGame(int x, int y) {
        double gx = (x - offsetX) / scale, gy = (y - offsetY) / scale;
        return gx >= 0 && gy >= 0 && gx < GameRules.WIDTH && gy < GameRules.HEIGHT
                ? Optional.of(new Point((int) gx, (int) gy))
                : Optional.empty();
    }
}
