package invaders.world;

import invaders.entity.Enemy;
import invaders.entity.Laser;
import invaders.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/** The sole owner of live simulation collections. Systems replace values through this API. */
public final class GameWorld {
    private Player player = Player.initial();
    private final List<Enemy> enemies = new ArrayList<>();
    private final List<Laser> lasers = new ArrayList<>();

    public Player player() {
        return player;
    }

    public List<Enemy> enemies() {
        return List.copyOf(enemies);
    }

    public List<Laser> lasers() {
        return List.copyOf(lasers);
    }

    public void setPlayer(Player value) {
        player = Objects.requireNonNull(value);
    }

    public void addEnemy(Enemy enemy) {
        enemies.add(Objects.requireNonNull(enemy));
    }

    public void addLaser(Laser laser) {
        lasers.add(Objects.requireNonNull(laser));
    }

    public void replaceEnemies(Collection<? extends Enemy> values) {
        List<Enemy> copy = List.copyOf(values);
        enemies.clear();
        enemies.addAll(copy);
    }

    public void replaceLasers(Collection<Laser> values) {
        List<Laser> copy = List.copyOf(values);
        lasers.clear();
        lasers.addAll(copy);
    }

    public void reset() {
        player = Player.initial();
        enemies.clear();
        lasers.clear();
    }
}
