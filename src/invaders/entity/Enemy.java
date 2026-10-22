package invaders.entity;

/**
 * Enemy behaviour consumed by movement and collision systems. New enemy types implement this
 * contract.
 */
public interface Enemy extends Entity {
    int direction();

    int speed();

    int health();

    int points();

    Enemy move(int dx, int dy);

    Enemy reverseAt(int x);

    Enemy takeHit();

    default boolean isDestroyed() {
        return health() <= 0;
    }
}
