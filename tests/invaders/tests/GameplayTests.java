package invaders.tests;

import static invaders.tests.TestSupport.*;

import invaders.control.GameKey;
import invaders.encounter.EncounterOutcome;
import invaders.entity.*;
import invaders.system.CollisionSystem;

import java.util.List;
import java.util.Random;

public final class GameplayTests {
    private GameplayTests() {}

    public static void run() throws Exception {
        test(
                "one laser hits the nearest enemy exactly once",
                () -> {
                    GameFixture f = GameFixture.started();
                    f.scenario(new Alien(100, 80, 1, 0), new Alien(100, 110, 1, 0));
                    f.world().addLaser(new Laser(100, 115));
                    f.game().update();
                    check(
                            f.game().snapshot().score() == 1 && f.world().lasers().isEmpty(),
                            "Consumed hit");
                    check(
                            f.world().enemies().stream().anyMatch(e -> e.y() == 80),
                            "Upper target survives");
                });
        test(
                "laser collision uses its 8-pixel width",
                () -> {
                    GameFixture f = GameFixture.started();
                    f.scenario(new Alien(120, 100, 1, 0));
                    f.world().addLaser(new Laser(100, 100));
                    f.game().update();
                    check(
                            f.game().snapshot().score() == 0 && f.world().lasers().size() == 1,
                            "No phantom hit");
                });
        test(
                "mothership full width takes three separate shots",
                () -> {
                    GameFixture f = GameFixture.started();
                    f.scenario(new Mothership(100, 100, 1), new Alien(400, 180, 1, 2));
                    for (int shot = 1; shot <= 3; shot++) {
                        Enemy boss =
                                f.world().enemies().stream()
                                        .filter(Mothership.class::isInstance)
                                        .findFirst()
                                        .orElseThrow();
                        f.world().addLaser(new Laser(boss.x() + 55, boss.y()));
                        f.game().update();
                        check(f.world().lasers().isEmpty(), "Boss consumes laser");
                        if (shot < 3) {
                            f.game().update();
                            check(
                                    f.world().enemies().stream()
                                                    .filter(Mothership.class::isInstance)
                                                    .findFirst()
                                                    .orElseThrow()
                                                    .health()
                                            == 3 - shot,
                                    "One damage per shot");
                        }
                    }
                    check(
                            f.game().snapshot().score() == 20
                                    && f.world().enemies().stream()
                                            .noneMatch(Mothership.class::isInstance),
                            "Award once");
                });
        test(
                "adjacent expired lasers are both removed",
                () -> {
                    GameFixture f = GameFixture.started();
                    f.world().addLaser(new Laser(0, -20));
                    f.world().addLaser(new Laser(20, -20));
                    f.game().update();
                    check(f.world().lasers().isEmpty(), "No skipped removal");
                });
        test(
                "a row reverses once when multiple members touch an edge",
                () -> {
                    GameFixture f = GameFixture.started();
                    f.scenario(new Alien(548, 20, 1, 0), new Alien(548, 60, 1, 0));
                    f.game().update();
                    List<Enemy> row =
                            f.world().enemies().stream()
                                    .filter(e -> e instanceof Alien a && a.row() == 0)
                                    .toList();
                    check(
                            row.size() == 2
                                    && row.stream()
                                            .allMatch(e -> e.direction() == -1 && e.x() == 550),
                            "One reversal");
                    check(row.get(0).y() == 35 && row.get(1).y() == 75, "One descent");
                });
        test(
                "simultaneous row turns descend once",
                () -> {
                    GameFixture f = GameFixture.started();
                    f.scenario(new Alien(548, 20, 1, 0), new Alien(2, 60, -1, 1));
                    f.game().update();
                    check(
                            f.world().enemies().stream()
                                    .anyMatch(
                                            e ->
                                                    e instanceof Alien a
                                                            && a.row() == 0
                                                            && e.y() == 35),
                            "Top drop once");
                    check(
                            f.world().enemies().stream()
                                    .anyMatch(
                                            e ->
                                                    e instanceof Alien a
                                                            && a.row() == 1
                                                            && e.y() == 75),
                            "Middle drop once");
                });
        test(
                "mothership stays completely inside the field",
                () -> {
                    GameFixture f = GameFixture.started();
                    f.scenario(new Mothership(534, 100, 1), new Alien(200, 180, 1, 2));
                    f.game().update();
                    Enemy boss =
                            f.world().enemies().stream()
                                    .filter(Mothership.class::isInstance)
                                    .findFirst()
                                    .orElseThrow();
                    check(
                            boss.x() == 536 && boss.direction() == -1 && boss.y() == 115,
                            "Full width clamped");
                });
        test(
                "opposing commands release independently",
                () -> {
                    GameFixture f = GameFixture.started();
                    f.game().press(GameKey.LEFT);
                    f.game().press(GameKey.RIGHT);
                    f.game().update();
                    check(f.world().player().x() == 40, "Cancel movement");
                    f.game().release(GameKey.LEFT);
                    f.game().update();
                    check(f.world().player().x() == 47, "Right stays held");
                });
        test(
                "player movement clamps at both original limits",
                () -> {
                    GameFixture f = GameFixture.started();
                    f.game().press(GameKey.RIGHT);
                    for (int i = 0; i < 100; i++) f.game().update();
                    check(f.world().player().x() == 550, "Right clamp");
                    f.game().release(GameKey.RIGHT);
                    f.game().press(GameKey.LEFT);
                    for (int i = 0; i < 100; i++) f.game().update();
                    check(f.world().player().x() == 5, "Left clamp");
                });
        test(
                "fire is edge-triggered",
                () -> {
                    GameFixture f = GameFixture.started();
                    f.game().press(GameKey.FIRE);
                    f.game().press(GameKey.FIRE);
                    check(f.world().lasers().size() == 1, "No repeat");
                    f.game().release(GameKey.FIRE);
                    f.game().press(GameKey.FIRE);
                    check(f.world().lasers().size() == 2, "Next press fires");
                });
        test(
                "finished encounter freezes and returns a defeat result",
                () -> {
                    GameFixture f = GameFixture.started();
                    f.scenario(new Alien(100, 350, 1, 0));
                    f.game().update();
                    check(
                            f.game().isFinished()
                                    && f.game().result().orElseThrow().outcome()
                                            == EncounterOutcome.DEFEAT,
                            "Result contract");
                    var snapshot = f.game().snapshot();
                    f.game().update();
                    check(f.game().snapshot().equals(snapshot), "Frozen");
                });
        test(
                "retry resets the existing world and queued events",
                () -> {
                    GameFixture f = GameFixture.started();
                    f.killOne();
                    f.game().press(GameKey.RIGHT);
                    f.game().press(GameKey.FIRE);
                    f.game().start();
                    check(
                            f.game().snapshot().score() == 0
                                    && f.world().player().x() == 40
                                    && f.world().lasers().isEmpty()
                                    && f.world().enemies().size() == 27
                                    && f.game().drainEvents().isEmpty(),
                            "Reset state");
                    check(f.game().result().isEmpty(), "New encounter has no result");
                    f.game().update();
                    check(f.world().player().x() == 40, "Reset input");
                });
        test(
                "fifty cleared formations retain exactly one new formation",
                () -> {
                    GameFixture f = GameFixture.started();
                    for (int i = 0; i < 50; i++) {
                        f.world().replaceEnemies(List.of());
                        f.game().update();
                        check(f.world().enemies().size() == 27, "No ghosts");
                    }
                    f.scenario(new Alien(200, 120, 1, 1), new Alien(400, 160, 1, 2));
                    f.game().update();
                    check(
                            f.world().enemies().stream()
                                            .filter(e -> e instanceof Alien a && a.row() == 0)
                                            .count()
                                    == 9,
                            "Safe staggered respawn");
                });
        test(
                "boss milestone triggers once and handles skipped scores",
                () -> {
                    GameFixture f = GameFixture.started();
                    for (int i = 0; i < 50; i++) f.killOne();
                    check(
                            f.game().snapshot().score() == 50
                                    && f.world().enemies().stream()
                                                    .filter(Mothership.class::isInstance)
                                                    .count()
                                            == 1,
                            "Milestone");
                    f.world()
                            .replaceEnemies(
                                    f.world().enemies().stream()
                                            .filter(e -> !(e instanceof Mothership))
                                            .toList());
                    f.game().update();
                    check(
                            f.world().enemies().stream().noneMatch(Mothership.class::isInstance),
                            "No repeated milestone");
                    GameFixture skipped = GameFixture.started();
                    for (int i = 0; i < 40; i++) skipped.killOne();
                    skipped.world().addEnemy(new Mothership(100, 100, 1));
                    for (int i = 0; i < 3; i++) {
                        Enemy boss =
                                skipped.world().enemies().stream()
                                        .filter(Mothership.class::isInstance)
                                        .findFirst()
                                        .orElseThrow();
                        skipped.world().addLaser(new Laser(boss.x() + 55, boss.y()));
                        skipped.game().update();
                    }
                    check(
                            skipped.game().snapshot().score() == 60
                                    && skipped.world().enemies().stream()
                                            .anyMatch(Mothership.class::isInstance),
                            "Crossed 50");
                });
        test(
                "snapshot values and collections cannot mutate the world",
                () -> {
                    GameFixture f = GameFixture.started();
                    var snapshot = f.game().snapshot();
                    try {
                        snapshot.enemies().clear();
                        throw new AssertionError("Mutable snapshot");
                    } catch (UnsupportedOperationException expected) {
                    }
                    f.game().update();
                    check(
                            snapshot.ticks() == 0 && snapshot.player().x() == 40,
                            "Stable old snapshot");
                });
        test(
                "thirty thousand ticks keep counts and bounds valid",
                () -> {
                    GameFixture f = GameFixture.started();
                    Random random = new Random(2018);
                    for (int tick = 0; tick < 30_000; tick++) {
                        if (f.game().isFinished()) f.game().start();
                        if (tick % 25 == 0) {
                            f.game().clearInput();
                            f.game().press(random.nextBoolean() ? GameKey.LEFT : GameKey.RIGHT);
                        }
                        if (tick % 4 == 0) {
                            f.game().release(GameKey.FIRE);
                            f.game().press(GameKey.FIRE);
                        }
                        f.game().update();
                        f.game().drainEvents();
                        check(
                                f.world().enemies().size() <= 28 && f.world().lasers().size() <= 40,
                                "Bounded counts");
                        check(
                                f.world().enemies().stream()
                                        .allMatch(
                                                e ->
                                                        e.x() >= 0
                                                                && e.bounds().right() <= 600
                                                                && e.health() > 0),
                                "Living objects valid");
                    }
                });
        test(
                "collision system accepts a new enemy implementation",
                () -> {
                    GameFixture f = GameFixture.started();
                    f.scenario(new CustomEnemy(100, 100, 2));
                    f.world().addLaser(new Laser(100, 100));
                    var result = new CollisionSystem().update(f.world());
                    check(
                            result.points() == 0 && f.world().enemies().get(0).health() == 1,
                            "Polymorphic damage");
                    f.world().addLaser(new Laser(100, 100));
                    result = new CollisionSystem().update(f.world());
                    check(
                            result.points() == 7 && f.world().enemies().isEmpty(),
                            "Custom award, no engine edits");
                });
    }

    private record CustomEnemy(int x, int y, int health) implements Enemy {
        public int width() {
            return 50;
        }

        public int height() {
            return 40;
        }

        public int direction() {
            return 1;
        }

        public int speed() {
            return 1;
        }

        public int points() {
            return 7;
        }

        public Enemy move(int dx, int dy) {
            return new CustomEnemy(x + dx, y + dy, health);
        }

        public Enemy reverseAt(int edge) {
            return new CustomEnemy(edge, y, health);
        }

        public Enemy takeHit() {
            return new CustomEnemy(x, y, health - 1);
        }
    }
}
