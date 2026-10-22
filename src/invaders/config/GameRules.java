package invaders.config;

/** Gameplay dimensions and tuning, independent of any window, sprite sheet or sound device. */
public final class GameRules {
    public static final int WIDTH = 600, HEIGHT = 450, PLAYER_Y = 350;
    public static final int PLAYER_MIN_X = 5, PLAYER_MAX_X = 550, ALIEN_MAX_X = 550;
    public static final int ROWS = 3, COLUMNS = 9, DESCENT = 15;
    public static final int TICKS_PER_SECOND = 60, MAX_CATCH_UP_STEPS = 5;

    private GameRules() {}
}
