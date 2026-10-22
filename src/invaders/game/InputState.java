package invaders.game;

import invaders.control.GameKey;

import java.util.EnumSet;

/** Held semantic commands, with edge detection for single-shot firing. */
final class InputState {
    private final EnumSet<GameKey> held = EnumSet.noneOf(GameKey.class);

    boolean press(GameKey key) {
        return held.add(key);
    }

    void release(GameKey key) {
        held.remove(key);
    }

    void clear() {
        held.clear();
    }

    int direction() {
        return (held.contains(GameKey.RIGHT) ? 1 : 0) - (held.contains(GameKey.LEFT) ? 1 : 0);
    }
}
