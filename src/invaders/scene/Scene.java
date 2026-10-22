package invaders.scene;

import invaders.control.GameControls;
import invaders.control.GameKey;

/** Scene behaviour and lifecycle. Rendering is a separate adapter, not a scene responsibility. */
public interface Scene extends GameControls, PointerInput {
    default void onEnter() {}

    default void onExit() {}

    default void update() {}

    default void press(GameKey key) {}

    default void release(GameKey key) {}

    default void clearInput() {}

    default void click(int x, int y) {}
}
