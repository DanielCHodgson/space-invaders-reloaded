package invaders.game;

import invaders.config.GameRules;
import invaders.control.GameKey;
import invaders.encounter.Encounter;
import invaders.encounter.EncounterOutcome;
import invaders.encounter.EncounterResult;
import invaders.entity.Laser;
import invaders.event.GameEvent;
import invaders.system.BossSpawnSystem;
import invaders.system.CollisionResult;
import invaders.system.CollisionSystem;
import invaders.system.FormationSystem;
import invaders.system.SpawnSystem;
import invaders.world.GameSnapshot;
import invaders.world.GameView;
import invaders.world.GameWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.random.RandomGenerator;

/**
 * Orchestrates a game run. Rules live in systems; menus, resources and platform APIs live
 * elsewhere.
 */
public final class GameSession implements Encounter, GameView {
    private final GameWorld world;
    private final InputState input = new InputState();
    private final CollisionSystem collisions = new CollisionSystem();
    private final FormationSystem formations = new FormationSystem();
    private final SpawnSystem spawns = new SpawnSystem();
    private final BossSpawnSystem bosses;
    private final List<GameEvent> events = new ArrayList<>();
    private int score;
    private long ticks;
    private boolean running, gameOver;

    public GameSession() {
        this(new GameWorld(), RandomGenerator.getDefault());
    }

    public GameSession(GameWorld world, RandomGenerator random) {
        this.world = world;
        bosses = new BossSpawnSystem(random);
    }

    public void start() {
        world.reset();
        input.clear();
        events.clear();
        bosses.reset();
        score = 0;
        ticks = 0;
        running = true;
        gameOver = false;
        spawns.populate(world);
    }

    public void press(GameKey key) {
        if (!running || !input.press(key)) return;
        if (key == GameKey.FIRE) {
            world.addLaser(new Laser(world.player().x() + 12, world.player().y()));
            events.add(GameEvent.SHOT);
        }
    }

    public void release(GameKey key) {
        input.release(key);
    }

    public void clearInput() {
        input.clear();
    }

    public void update() {
        if (!running) return;
        ticks++;
        world.setPlayer(world.player().move(input.direction()));
        CollisionResult result = collisions.update(world);
        score += result.points();
        for (int hit = 0; hit < result.hits(); hit++) events.add(GameEvent.EXPLOSION);
        formations.update(world);
        if (world.enemies().stream().anyMatch(e -> e.y() >= GameRules.PLAYER_Y)) {
            gameOver = true;
            running = false;
            clearInput();
            return;
        }
        spawns.update(world);
        bosses.update(world, score);
    }

    public boolean isFinished() {
        return gameOver;
    }

    @Override
    public Optional<EncounterResult> result() {
        return gameOver
                ? Optional.of(new EncounterResult(EncounterOutcome.DEFEAT, score))
                : Optional.empty();
    }

    public List<GameEvent> drainEvents() {
        List<GameEvent> drained = List.copyOf(events);
        events.clear();
        return drained;
    }

    @Override
    public GameSnapshot snapshot() {
        return new GameSnapshot(
                world.player(), world.enemies(), world.lasers(), score, ticks, gameOver);
    }
}
