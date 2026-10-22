package invaders.entity;

public record Mothership(int x, int y, int direction, int health) implements Enemy {
    public Mothership(int x, int y, int direction) {
        this(x, y, direction, 3);
    }

    public Mothership {
        if (direction != -1 && direction != 1)
            throw new IllegalArgumentException("Direction must be -1 or 1");
    }

    public int width() {
        return 64;
    }

    public int height() {
        return 32;
    }

    public int speed() {
        return 6;
    }

    public int points() {
        return 20;
    }

    public Mothership move(int dx, int dy) {
        return new Mothership(x + dx, y + dy, direction, health);
    }

    public Mothership reverseAt(int edge) {
        return new Mothership(edge, y, -direction, health);
    }

    public Mothership takeHit() {
        return new Mothership(x, y, direction, health - 1);
    }
}
