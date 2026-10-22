package invaders.scene;

import invaders.control.GameControls;
import invaders.control.GameKey;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Dispatches input and ticks to one active scene. Overlay history preserves the underlying game.
 */
public final class SceneManager implements SceneNavigator, SceneView, GameControls, PointerInput {
    private final Map<SceneId, Scene> scenes = new EnumMap<>(SceneId.class);
    private final Deque<SceneId> history = new ArrayDeque<>();
    private final Runnable quit;
    private SceneId currentId;

    public SceneManager(Runnable quit) {
        this.quit = quit;
    }

    public void register(SceneId id, Scene scene) {
        if (scenes.putIfAbsent(Objects.requireNonNull(id), Objects.requireNonNull(scene)) != null)
            throw new IllegalArgumentException("Scene already registered: " + id);
    }

    private void activate(SceneId id) {
        if (!scenes.containsKey(id))
            throw new IllegalArgumentException("Unregistered scene: " + id);
        if (currentId != null) {
            active().clearInput();
            active().onExit();
        }
        currentId = id;
        active().onEnter();
    }

    @Override
    public void show(SceneId id) {
        if (!scenes.containsKey(id))
            throw new IllegalArgumentException("Unregistered scene: " + id);
        history.clear();
        activate(id);
    }

    @Override
    public void push(SceneId id) {
        if (!scenes.containsKey(id))
            throw new IllegalArgumentException("Unregistered scene: " + id);
        if (currentId != null) history.push(currentId);
        activate(id);
    }

    @Override
    public void back() {
        if (!history.isEmpty()) activate(history.pop());
    }

    @Override
    public void quit() {
        quit.run();
    }

    @Override
    public SceneId currentId() {
        return currentId;
    }

    private Scene active() {
        if (currentId == null) throw new IllegalStateException("No active scene");
        return scenes.get(currentId);
    }

    public void update() {
        active().update();
    }

    @Override
    public void press(GameKey key) {
        active().press(key);
    }

    @Override
    public void release(GameKey key) {
        active().release(key);
    }

    @Override
    public void clearInput() {
        if (currentId != null) active().clearInput();
    }

    @Override
    public void click(int x, int y) {
        active().click(x, y);
    }
}
