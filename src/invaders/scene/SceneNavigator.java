package invaders.scene;

public interface SceneNavigator {
    void show(SceneId id);

    void push(SceneId id);

    void back();

    void quit();
}
