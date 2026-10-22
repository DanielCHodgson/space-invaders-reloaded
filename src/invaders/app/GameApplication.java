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

import java.io.IOException;

import javax.swing.JOptionPane;
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

    private GameApplication(SpriteSource sprites, GameAudio audio) {
        if (!SwingUtilities.isEventDispatchThread())
            throw new IllegalStateException("Create UI on EDT");
        this.audio = audio;
        audioDirector = new AudioDirector(audio);
        MenuLayout menus = new MenuLayout();
        scenes = GameAssembly.scenes(game, menus, this::close);
        panel = new GamePanel(GameAssembly.renderer(sprites, game, scenes, menus));
        input = new InputHandler(scenes, scenes, panel);
        input.attach();
        window = new GameWindow(panel, this::close);
        loop = new SwingGameLoop(scenes::update, this::present);
    }

    public static void launch() {
        GameAudio audio = null;
        try {
            ResourceLoader resources = new ClasspathResourceLoader();
            AssetCatalog catalog = AssetCatalog.load(resources);
            SpriteSource sprites = new SpriteLibrary(resources, catalog);
            audio = new AudioPlayer(new JavaSoundClipLoader(resources, catalog));
            audio.preload();
            GameAudio loadedAudio = audio;
            SwingUtilities.invokeLater(
                    () -> {
                        try {
                            GameApplication app = new GameApplication(sprites, loadedAudio);
                            app.start();
                        } catch (RuntimeException e) {
                            loadedAudio.close();
                            showError(e);
                        }
                    });
        } catch (IOException | RuntimeException e) {
            if (audio != null) audio.close();
            SwingUtilities.invokeLater(() -> showError(e));
        }
    }

    private static void showError(Exception error) {
        error.printStackTrace();
        JOptionPane.showMessageDialog(
                null,
                error.getMessage(),
                "Space Invaders could not start",
                JOptionPane.ERROR_MESSAGE);
    }

    private void start() {
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
