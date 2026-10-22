package invaders.entity;

public record Alien(int x, int y, int direction, int row, int health) implements Enemy {
    public Alien(int x, int y, int direction, int row) {
        this(x, y, direction, row, 1);
    }

    public Alien {
        if (row < 0 || row >= invaders.config.GameRules.ROWS)
            throw new IllegalArgumentException("Invalid row");
        if (direction != -1 && direction != 1)
            throw new IllegalArgumentException("Direction must be -1 or 1");
    }

    public int width() {
        return 32;
    }

    public int height() {
        return 32;
    }

    public int speed() {
        return 2;
    }

    public int points() {
        return 1;
    }

    public Alien move(int dx, int dy) {
        return new Alien(x + dx, y + dy, direction, row, health);
    }

    public Alien reverseAt(int edge) {
        return new Alien(edge, y, -direction, row, health);
    }

    public Alien takeHit() {
        return new Alien(x, y, direction, row, health - 1);
    }
}
