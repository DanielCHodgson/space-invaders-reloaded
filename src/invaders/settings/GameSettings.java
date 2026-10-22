package invaders.settings;

/** User preferences, independent of Swing and physical monitor handles. */
public record GameSettings(
        DisplayMode mode, int windowWidth, int windowHeight, boolean showLauncher) {
    public GameSettings {
        if (mode == null
                || windowWidth < 480
                || windowWidth > 7680
                || windowHeight < 360
                || windowHeight > 4320)
            throw new IllegalArgumentException(
                    "Window size must be between 480 x 360 and 7680 x 4320.");
    }

    public static GameSettings defaults() {
        return new GameSettings(DisplayMode.BORDERLESS, 960, 720, true);
    }

    public GameSettings withMode(DisplayMode value) {
        return new GameSettings(value, windowWidth, windowHeight, showLauncher);
    }

    public GameSettings withWindowSize(int width, int height) {
        return new GameSettings(mode, width, height, showLauncher);
    }
}
