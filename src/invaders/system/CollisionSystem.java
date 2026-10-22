package invaders.system;

import invaders.entity.Enemy;
import invaders.entity.Laser;
import invaders.world.GameWorld;

import java.util.ArrayList;
import java.util.List;

/** Consumes a projectile and applies damage atomically to its nearest intersecting enemy. */
public final class CollisionSystem {
    public CollisionResult update(GameWorld world) {
        List<Enemy> enemies = new ArrayList<>(world.enemies());
        List<Laser> survivors = new ArrayList<>();
        int points = 0, hits = 0;
        for (Laser laser : world.lasers()) {
            int nearest = -1, nearestBottom = Integer.MIN_VALUE;
            for (int i = 0; i < enemies.size(); i++) {
                Enemy enemy = enemies.get(i);
                if (laser.sweptBounds().intersects(enemy.bounds())
                        && enemy.bounds().bottom() > nearestBottom) {
                    nearest = i;
                    nearestBottom = enemy.bounds().bottom();
                }
            }
            if (nearest >= 0) {
                Enemy damaged = enemies.get(nearest).takeHit();
                if (damaged.isDestroyed()) {
                    points += damaged.points();
                    enemies.remove(nearest);
                } else enemies.set(nearest, damaged);
                hits++;
            } else if (laser.y() - Laser.SPEED + laser.height() > 0) {
                survivors.add(laser.advance());
            }
        }
        world.replaceEnemies(enemies);
        world.replaceLasers(survivors);
        return new CollisionResult(points, hits);
    }
}
