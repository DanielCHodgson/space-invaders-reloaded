package invaders.entity;

import invaders.config.GameRules;

public record Player(int x, int y) implements Entity {
    public static Player initial() {
        return new Player(40, GameRules.PLAYER_Y);
    }

    public int width() {
        return 32;
    }

    public int height() {
        return 32;
    }

    public Player move(int direction) {
        return new Player(
                Math.clamp(x + direction * 7, GameRules.PLAYER_MIN_X, GameRules.PLAYER_MAX_X), y);
    }
}
