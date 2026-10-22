package invaders.tests;

import static invaders.tests.TestSupport.*;

import invaders.settings.*;

import java.nio.file.*;

public final class SettingsTests {
    private SettingsTests() {}

    public static void run() throws Exception {
        Path directory = Files.createTempDirectory("invaders-settings-test-");
        Path file = directory.resolve("settings.ini");
        SettingsStore store = new SettingsStore(file);
        try {
            test(
                    "missing INI uses defaults",
                    () -> check(store.load().equals(GameSettings.defaults()), "Defaults"));
            test(
                    "INI round trip preserves every display preference",
                    () -> {
                        for (DisplayMode mode : DisplayMode.values()) {
                            GameSettings selected = new GameSettings(mode, 1280, 720, false);
                            store.save(selected);
                            check(store.load().equals(selected), "Round trip " + mode);
                            check(Files.readString(file).contains("[display]"), "Sectioned INI");
                        }
                        try (var files = Files.list(directory)) {
                            check(files.count() == 1, "No temporary files remain");
                        }
                    });
            test(
                    "INI accepts comments and casing and isolates sections",
                    () -> {
                        Files.writeString(
                                file,
                                "; comment\n"
                                    + "# comment\n"
                                    + "[DISPLAY]\n"
                                    + "MODE=windowed\n"
                                    + "window_width=1600\n"
                                    + "window_height=900\n"
                                    + "[other]\n"
                                    + "mode=FULLSCREEN\n"
                                    + "[launcher]\n"
                                    + "show_at_startup=FALSE\n");
                        check(
                                store.load()
                                        .equals(
                                                new GameSettings(
                                                        DisplayMode.WINDOWED, 1600, 900, false)),
                                "Parsed sections");
                    });
            test(
                    "malformed INI fields fall back independently",
                    () -> {
                        Files.writeString(
                                file,
                                "[display]\n"
                                    + "mode=oops\n"
                                    + "window_width=-1\n"
                                    + "window_height=900\n"
                                    + "[launcher]\n"
                                    + "show_at_startup=oops\n");
                        GameSettings defaults = GameSettings.defaults();
                        check(
                                store.load()
                                        .equals(
                                                new GameSettings(
                                                        defaults.mode(),
                                                        defaults.windowWidth(),
                                                        900,
                                                        defaults.showLauncher())),
                                "Independent fallback");
                    });
        } finally {
            Files.deleteIfExists(file);
            Files.deleteIfExists(directory);
        }
    }
}
