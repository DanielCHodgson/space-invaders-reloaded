package invaders.settings;

public enum DisplayMode {
    WINDOWED("Windowed"),
    BORDERLESS("Borderless fullscreen"),
    FULLSCREEN("Exclusive fullscreen");
    private final String label;

    DisplayMode(String label) {
        this.label = label;
    }

    public DisplayMode next() {
        return values()[(ordinal() + 1) % values().length];
    }

    @Override
    public String toString() {
        return label;
    }
}
