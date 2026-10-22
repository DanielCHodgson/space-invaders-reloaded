package invaders.system;

import invaders.config.GameRules;
import invaders.entity.Alien;
import invaders.world.GameWorld;

/** The original staggered alien-row spawning policy, separate from movement and damage. */
public final class SpawnSystem {
    public void populate(GameWorld world) {
        for (int row = 0; row < GameRules.ROWS; row++) spawnRow(world, row);
    }

    private void spawnRow(GameWorld world, int row) {
        for (int column = 0; column < GameRules.COLUMNS; column++)
            world.addEnemy(new Alien(130 + column * 40, 20 + row * 40, row == 1 ? -1 : 1, row));
    }

    public void update(GameWorld world) {
        if (world.enemies().stream().noneMatch(Alien.class::isInstance)) {
            populate(world);
            return;
        }
        for (int row = 0; row < GameRules.ROWS; row++) {
            final int target = row;
            if (world.enemies().stream().anyMatch(e -> e instanceof Alien a && a.row() == target))
                continue;
            int low = row == 0 ? Integer.MIN_VALUE : row == 1 ? 20 : 65;
            int high = row == 0 ? 55 : row == 1 ? 100 : 135;
            boolean clear =
                    world.enemies().stream()
                            .filter(Alien.class::isInstance)
                            .noneMatch(e -> e.y() > low && e.y() < high);
            if (clear) spawnRow(world, row);
        }
    }
}
