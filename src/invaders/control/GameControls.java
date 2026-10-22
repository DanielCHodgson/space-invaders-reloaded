package invaders.control;

public interface GameControls {
    void press(GameKey key);

    void release(GameKey key);

    void clearInput();
}
