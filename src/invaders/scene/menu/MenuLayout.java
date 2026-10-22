package invaders.scene.menu;

import invaders.scene.SceneId;

import java.util.List;

/** One source of truth for button labels, drawing geometry and pointer hit testing. */
public final class MenuLayout {
    public List<MenuButton> buttons(SceneId scene) {
        return switch (scene) {
            case MENU ->
                    List.of(
                            button(MenuAction.START, "Play", 150),
                            button(MenuAction.HELP, "Help", 225),
                            button(MenuAction.QUIT, "Quit", 300));
            case GAME_OVER ->
                    List.of(
                            button(MenuAction.START, "Retry", 150),
                            button(MenuAction.HELP, "Help", 225),
                            button(MenuAction.QUIT, "Rage Quit", 300));
            case HELP -> List.of(button(MenuAction.BACK, "Back", 300));
            case PLAYING -> List.of();
        };
    }

    private MenuButton button(MenuAction action, String label, int y) {
        return new MenuButton(action, label, 225, y, 150, 50);
    }

    public MenuAction actionAt(SceneId scene, int x, int y) {
        return buttons(scene).stream()
                .filter(b -> b.contains(x, y))
                .map(MenuButton::action)
                .findFirst()
                .orElse(MenuAction.NONE);
    }
}
