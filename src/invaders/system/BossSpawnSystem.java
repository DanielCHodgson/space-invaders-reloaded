package invaders.system;

import invaders.entity.Mothership;
import invaders.world.GameWorld;

import java.util.random.RandomGenerator;

/** Remembers crossed score milestones, including jumps caused by boss awards. */
public final class BossSpawnSystem {
    private final RandomGenerator random;
    private int nextScore = 50;

    public BossSpawnSystem(RandomGenerator random) {
        this.random = random;
    }

    public void reset() {
        nextScore = 50;
    }

    public void update(GameWorld world, int score) {
        if (score >= nextScore
                && world.enemies().stream().noneMatch(Mothership.class::isInstance)
                && world.enemies().stream().allMatch(e -> e.y() >= 55)) {
            world.addEnemy(new Mothership(random.nextInt(501), 10, 1));
            nextScore = (score / 50 + 1) * 50;
        }
    }
}
