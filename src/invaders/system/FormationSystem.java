package invaders.system;

import invaders.config.GameRules;
import invaders.entity.Alien;
import invaders.entity.Enemy;
import invaders.world.GameWorld;

import java.util.ArrayList;
import java.util.List;

/** Alien rows turn as formations. Independently moving enemies use their own width and speed. */
public final class FormationSystem {
    public void update(GameWorld world) {
        List<Enemy> enemies = world.enemies();
        boolean[] turns = new boolean[GameRules.ROWS];
        boolean descend = false;
        for (Enemy enemy : enemies) {
            if (reachesEdge(enemy)) {
                descend = true;
                if (enemy instanceof Alien alien) turns[alien.row()] = true;
            }
        }
        List<Enemy> moved = new ArrayList<>(enemies.size());
        for (Enemy enemy : enemies) {
            int nextX = enemy.x() + enemy.direction() * enemy.speed();
            boolean turn = enemy instanceof Alien alien ? turns[alien.row()] : reachesEdge(enemy);
            Enemy next =
                    enemy.move(enemy.direction() * enemy.speed(), descend ? GameRules.DESCENT : 0);
            if (turn) next = next.reverseAt(Math.clamp(nextX, 0, maximumX(enemy)));
            moved.add(next);
        }
        world.replaceEnemies(moved);
    }

    private boolean reachesEdge(Enemy enemy) {
        int nextX = enemy.x() + enemy.direction() * enemy.speed();
        return nextX <= 0 || nextX >= maximumX(enemy);
    }

    private int maximumX(Enemy enemy) {
        return enemy instanceof Alien ? GameRules.ALIEN_MAX_X : GameRules.WIDTH - enemy.width();
    }
}
