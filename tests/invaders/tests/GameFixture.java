package invaders.tests;

import invaders.entity.*;
import invaders.game.GameSession;
import invaders.world.GameWorld;

import java.util.List;
import java.util.Random;

public record GameFixture(GameWorld world, GameSession game) {
    public static GameFixture started() {
        GameWorld world = new GameWorld();
        GameSession game = new GameSession(world, new Random(18));
        game.start();
        return new GameFixture(world, game);
    }

    public void scenario(Enemy... enemies) {
        world.replaceEnemies(List.of(enemies));
        world.replaceLasers(List.of());
    }

    public void killOne() {
        scenario(
                new Alien(100, 100, 1, 0),
                new Alien(250, 100, 1, 0),
                new Alien(300, 140, -1, 1),
                new Alien(400, 180, 1, 2));
        world.addLaser(new Laser(100, 100));
        game.update();
    }
}
