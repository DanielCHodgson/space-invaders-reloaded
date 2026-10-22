package invaders.world;

import invaders.entity.Enemy;
import invaders.entity.Laser;
import invaders.entity.Player;

import java.util.List;

/** Immutable render data. Views never receive the mutable world or session. */
public record GameSnapshot(
        Player player,
        List<Enemy> enemies,
        List<Laser> lasers,
        int score,
        long ticks,
        boolean gameOver) {
    public GameSnapshot {
        enemies = List.copyOf(enemies);
        lasers = List.copyOf(lasers);
    }
}
