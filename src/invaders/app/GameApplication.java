package invaders.app;

import invaders.asset.*;
import invaders.audio.*;
import invaders.game.GameSession;
import invaders.input.InputHandler;
import invaders.loop.GameLoop;
import invaders.loop.SwingGameLoop;
import invaders.scene.SceneManager;
import invaders.scene.menu.MenuLayout;
import invaders.ui.GamePanel;
import invaders.ui.GameWindow;

import javax.swing.SwingUtilities;

/** Application lifecycle and dependency wiring; no gameplay rules live here. */
public final class GameApplication implements AutoCloseable {
    private final GameSession game = new GameSession();
    private final GameAudio audio;
    private final AudioDirector audioDirector;
    private final SceneManager scenes;
    private final GamePanel panel;
    private final InputHandler input;
    private final GameWindow window;
    private final GameLoop loop;
    private boolean closed;

    GameApplication(
            SpriteSource sprites,
            GameAudio audio,
            invaders.settings.GameSettings settings,
            invaders.settings.SettingsStore store) {
        if (!SwingUtilities.isEventDispatchThread())
            throw new IllegalStateException("Create UI on EDT");
        this.audio = audio;
        audioDirector = new AudioDirector(audio);
        MenuLayout menus = new MenuLayout();
        scenes = GameAssembly.scenes(game, menus, this::close);
        panel = new GamePanel(GameAssembly.renderer(sprites, game, scenes, menus));
        input = new InputHandler(scenes, scenes, panel);
        input.attach();
        window =
                new GameWindow(
                        panel,
                        this::close,
                        settings,
                        this::showSettings,
                        value -> GameLauncher.save(store, value));
        loop = new SwingGameLoop(scenes::update, this::present);
    }

    public static void launch() {
        GameLauncher.launch();
    }

    private void showSettings() {
        loop.close();
        try {
            window.configure();
        } finally {
            if (!closed) loop.start();
        }
    }

    void start() {
        window.open();
        present();
        loop.start();
    }

    private void present() {
        audioDirector.update(scenes.currentId(), game.drainEvents());
        panel.repaint();
    }

    @Override
    public void close() {
        if (closed) return;
        closed = true;
        loop.close();
        input.detach();
        audio.close();
        window.close();
    }
}
