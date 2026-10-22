package invaders.scene;

import invaders.scene.menu.MenuAction;
import invaders.scene.menu.MenuLayout;

public final class HelpScene implements Scene {
    private final SceneNavigator navigation;
    private final MenuLayout layout;

    public HelpScene(SceneNavigator navigation, MenuLayout layout) {
        this.navigation = navigation;
        this.layout = layout;
    }

    @Override
    public void click(int x, int y) {
        if (layout.actionAt(SceneId.HELP, x, y) == MenuAction.BACK) navigation.back();
    }
}
