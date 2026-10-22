package invaders.scene;

import invaders.encounter.Encounter;
import invaders.scene.menu.MenuLayout;

/** Shared menu behaviour for the main menu and game-over screen, configured by scene id. */
public final class MenuScene implements Scene {
    private final SceneId id;
    private final Encounter game;
    private final SceneNavigator navigation;
    private final MenuLayout layout;

    public MenuScene(SceneId id, Encounter game, SceneNavigator navigation, MenuLayout layout) {
        this.id = id;
        this.game = game;
        this.navigation = navigation;
        this.layout = layout;
    }

    @Override
    public void click(int x, int y) {
        switch (layout.actionAt(id, x, y)) {
            case START -> {
                game.start();
                navigation.show(SceneId.PLAYING);
            }
            case HELP -> navigation.push(SceneId.HELP);
            case QUIT -> navigation.quit();
            default -> {}
        }
    }
}
