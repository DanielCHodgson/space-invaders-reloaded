package invaders.ui;

import invaders.settings.DisplayMode;
import invaders.settings.GameSettings;

import java.awt.Component;
import java.awt.GridLayout;
import java.util.Optional;

import javax.swing.*;

/** Shared startup launcher and in-game display menu. Cancel never changes saved preferences. */
public final class DisplaySettingsDialog {
    private DisplaySettingsDialog() {}

    public static Optional<GameSettings> choose(
            Component parent, GameSettings initial, String acceptLabel) {
        JComboBox<DisplayMode> mode = new JComboBox<>(DisplayMode.values());
        mode.setSelectedItem(initial.mode());
        JComboBox<String> sizes =
                new JComboBox<>(
                        new String[] {
                            "960 x 720", "1280 x 720", "1600 x 900", "1920 x 1080", "2560 x 1440"
                        });
        sizes.setEditable(true);
        sizes.setSelectedItem(initial.windowWidth() + " x " + initial.windowHeight());
        JCheckBox show = new JCheckBox("Show launcher at startup", initial.showLauncher());
        JPanel fields = new JPanel(new GridLayout(0, 1, 8, 8));
        fields.add(new JLabel("Display mode"));
        fields.add(mode);
        fields.add(new JLabel("Window size (windowed mode)"));
        fields.add(sizes);
        fields.add(show);
        fields.add(new JLabel("F10: settings   |   F11: cycle modes   |   Esc: windowed"));
        while (true) {
            int result =
                    JOptionPane.showOptionDialog(
                            parent,
                            fields,
                            "Space Invaders: Reloaded",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            new String[] {acceptLabel, "Cancel"},
                            acceptLabel);
            if (result != 0) return Optional.empty();
            try {
                String[] dimensions =
                        sizes.getEditor().getItem().toString().strip().split("(?i)\\s*[x×]\\s*");
                if (dimensions.length != 2)
                    throw new IllegalArgumentException("Enter a window size such as 1280 x 720.");
                return Optional.of(
                        new GameSettings(
                                (DisplayMode) mode.getSelectedItem(),
                                Integer.parseInt(dimensions[0]),
                                Integer.parseInt(dimensions[1]),
                                show.isSelected()));
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(
                        parent,
                        e.getMessage(),
                        "Invalid display settings",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}
