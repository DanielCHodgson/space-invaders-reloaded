package invaders.entity;

/** Axis-aligned simulation geometry. No dependency on AWT or a rendering backend. */
public record Bounds(int x, int y, int width, int height) {
    public Bounds {
        if (width <= 0 || height <= 0)
            throw new IllegalArgumentException("Bounds must have positive size");
    }

    public int right() {
        return x + width;
    }

    public int bottom() {
        return y + height;
    }

    public boolean intersects(Bounds other) {
        return x < other.right() && right() > other.x && y < other.bottom() && bottom() > other.y;
    }
}
