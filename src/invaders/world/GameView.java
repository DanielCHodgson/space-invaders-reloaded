package invaders.world;

@FunctionalInterface
public interface GameView {
    GameSnapshot snapshot();
}
