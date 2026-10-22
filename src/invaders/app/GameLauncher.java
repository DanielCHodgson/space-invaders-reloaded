package invaders.app;

import invaders.asset.*;
import invaders.audio.*;
import invaders.settings.GameSettings;
import invaders.settings.SettingsStore;
import invaders.ui.DisplaySettingsDialog;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import javax.swing.*;

/** Startup preferences first, then resource loading off the UI thread. */
final class GameLauncher {
    private GameLauncher() {}

    static void launch() {
        SwingUtilities.invokeLater(GameLauncher::chooseAndLoad);
    }

    private static void chooseAndLoad() {
        SettingsStore store = SettingsStore.forCurrentUser();
        GameSettings initial;
        try {
            initial = store.load();
        } catch (IOException e) {
            initial = GameSettings.defaults();
            warn(e);
        }
        Optional<GameSettings> selected =
                initial.showLauncher()
                        ? DisplaySettingsDialog.choose(null, initial, "Play")
                        : Optional.of(initial);
        if (selected.isEmpty()) return;
        GameSettings settings = selected.orElseThrow();
        save(store, settings);
        new SwingWorker<LoadedResources, Void>() {
            @Override
            protected LoadedResources doInBackground() throws IOException {
                ResourceLoader resources = new ClasspathResourceLoader();
                AssetCatalog catalog = AssetCatalog.load(resources);
                SpriteSource sprites = new SpriteLibrary(resources, catalog);
                GameAudio audio = new AudioPlayer(new JavaSoundClipLoader(resources, catalog));
                audio.preload();
                return new LoadedResources(sprites, audio);
            }

            @Override
            protected void done() {
                LoadedResources loaded = null;
                try {
                    loaded = get();
                    new GameApplication(loaded.sprites(), loaded.audio(), settings, store).start();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    if (loaded != null) loaded.audio().close();
                    error(e);
                } catch (ExecutionException | RuntimeException e) {
                    if (loaded != null) loaded.audio().close();
                    error(e.getCause() == null ? e : e.getCause());
                }
            }
        }.execute();
    }

    static void save(SettingsStore store, GameSettings settings) {
        try {
            store.save(settings);
        } catch (IOException e) {
            warn(e);
        }
    }

    private static void warn(IOException e) {
        JOptionPane.showMessageDialog(
                null,
                "Could not read or save settings.ini: " + e.getMessage(),
                "Display settings",
                JOptionPane.WARNING_MESSAGE);
    }

    private static void error(Throwable e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(
                null, e.getMessage(), "Space Invaders could not start", JOptionPane.ERROR_MESSAGE);
    }

    private record LoadedResources(SpriteSource sprites, GameAudio audio) {}
}
