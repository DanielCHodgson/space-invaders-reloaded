package invaders.settings;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/** Sectioned INI persistence. Atomic replacement prevents a partial file on interrupted saves. */
public final class SettingsStore {
    private final Path file;

    public SettingsStore(Path file) {
        this.file = file.toAbsolutePath();
    }

    public static SettingsStore forCurrentUser() {
        String local = System.getenv("LOCALAPPDATA");
        Path directory =
                local == null || local.isBlank()
                        ? Path.of(System.getProperty("user.home"), ".space-invaders-reloaded")
                        : Path.of(local, "SpaceInvadersReloaded");
        return new SettingsStore(directory.resolve("settings.ini"));
    }

    public Path file() {
        return file;
    }

    public GameSettings load() throws IOException {
        GameSettings defaults = GameSettings.defaults();
        if (!Files.exists(file)) return defaults;
        Map<String, String> values = new HashMap<>();
        String section = "";
        for (String raw : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            String line = raw.strip();
            if (line.isEmpty() || line.startsWith(";") || line.startsWith("#")) continue;
            if (line.startsWith("[") && line.endsWith("]")) {
                section = line.substring(1, line.length() - 1).strip().toLowerCase(Locale.ROOT);
                continue;
            }
            int equals = line.indexOf('=');
            if (equals > 0)
                values.put(
                        section + "." + line.substring(0, equals).strip().toLowerCase(Locale.ROOT),
                        line.substring(equals + 1).strip());
        }
        DisplayMode mode = defaults.mode();
        try {
            mode =
                    DisplayMode.valueOf(
                            values.getOrDefault("display.mode", mode.name())
                                    .toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
        }
        int width = number(values.get("display.window_width"), defaults.windowWidth(), 480, 7680);
        int height =
                number(values.get("display.window_height"), defaults.windowHeight(), 360, 4320);
        String show =
                values.getOrDefault(
                        "launcher.show_at_startup", Boolean.toString(defaults.showLauncher()));
        boolean showLauncher =
                show.equalsIgnoreCase("true")
                        || (!show.equalsIgnoreCase("false") && defaults.showLauncher());
        return new GameSettings(mode, width, height, showLauncher);
    }

    private static int number(String text, int fallback, int minimum, int maximum) {
        try {
            int value = Integer.parseInt(text);
            return value >= minimum && value <= maximum ? value : fallback;
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    public void save(GameSettings settings) throws IOException {
        Files.createDirectories(file.getParent());
        String contents =
                "; Space Invaders: Reloaded - display preferences\n"
                        + "[display]\nmode="
                        + settings.mode().name()
                        + "\nwindow_width="
                        + settings.windowWidth()
                        + "\nwindow_height="
                        + settings.windowHeight()
                        + "\n\n[launcher]\nshow_at_startup="
                        + settings.showLauncher()
                        + "\n";
        Path temporary = Files.createTempFile(file.getParent(), "settings-", ".tmp");
        try {
            Files.writeString(temporary, contents, StandardCharsets.UTF_8);
            try {
                Files.move(
                        temporary,
                        file,
                        StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporary);
        }
    }
}
