package invaders.entity;

public record Laser(int x, int y) implements Entity {
    public static final int SPEED = 10;

    public int width() {
        return 8;
    }

    public int height() {
        return 16;
    }

    public Bounds sweptBounds() {
        return new Bounds(x, y - SPEED, width(), height() + SPEED);
    }

    public Laser advance() {
        return new Laser(x, y - SPEED);
    }
}
