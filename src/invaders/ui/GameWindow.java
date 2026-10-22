package invaders.ui;

import invaders.settings.DisplayMode;
import invaders.settings.GameSettings;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.*;
import java.util.function.Consumer;

import javax.swing.*;

/** Owns desktop display modes; gameplay and viewport coordinates remain independent. */
public final class GameWindow implements AutoCloseable {
    private final JFrame frame = new JFrame("Space Invaders");
    private final GamePanel panel;
    private final Consumer<GameSettings> onSettingsChanged;
    private final Timer resizeSave;
    private GameSettings settings;
    private GraphicsDevice fullscreenDevice;
    private Point windowedLocation;
    private boolean changingMode;
    private boolean closed;

    public GameWindow(
            GamePanel panel,
            Runnable onClose,
            GameSettings settings,
            Runnable openSettings,
            Consumer<GameSettings> onSettingsChanged) {
        this.panel = panel;
        this.settings = settings;
        this.onSettingsChanged = onSettingsChanged;
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent event) {
                        onClose.run();
                    }
                });
        resizeSave = new Timer(250, event -> rememberWindowedSize());
        resizeSave.setRepeats(false);
        frame.addComponentListener(
                new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent event) {
                        if (!changingMode && !closed && settings().mode() == DisplayMode.WINDOWED)
                            resizeSave.restart();
                    }
                });
        bind("display-settings", KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0, true), openSettings);
        bind(
                "cycle-display",
                KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0, true),
                () -> setMode(settings().mode().next()));
        bind(
                "toggle-fullscreen",
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.ALT_DOWN_MASK, true),
                () ->
                        setMode(
                                settings().mode() == DisplayMode.WINDOWED
                                        ? DisplayMode.FULLSCREEN
                                        : DisplayMode.WINDOWED));
        bind(
                "return-windowed",
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true),
                () -> {
                    if (settings().mode() != DisplayMode.WINDOWED) setMode(DisplayMode.WINDOWED);
                });
    }

    private void bind(String name, KeyStroke key, Runnable action) {
        JRootPane root = frame.getRootPane();
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key, name);
        root.getActionMap()
                .put(
                        name,
                        new AbstractAction() {
                            @Override
                            public void actionPerformed(ActionEvent event) {
                                action.run();
                            }
                        });
    }

    public GameSettings settings() {
        return settings;
    }

    public void open() {
        apply(settings);
    }

    public void configure() {
        rememberWindowedSize();
        DisplaySettingsDialog.choose(frame, settings, "Apply").ifPresent(this::apply);
    }

    public void setMode(DisplayMode mode) {
        if (closed || mode == settings.mode()) return;
        rememberWindowedSize();
        apply(settings.withMode(mode));
    }

    private void apply(GameSettings requested) {
        if (closed) return;
        changingMode = true;
        resizeSave.stop();
        GraphicsDevice device =
                frame.getGraphicsConfiguration() == null
                        ? GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
                        : frame.getGraphicsConfiguration().getDevice();
        if (frame.isVisible() && settings.mode() == DisplayMode.WINDOWED)
            windowedLocation = frame.getLocation();
        releaseFullscreen();
        frame.dispose();
        DisplayMode actual = requested.mode();
        if (actual == DisplayMode.FULLSCREEN && !device.isFullScreenSupported())
            actual = DisplayMode.BORDERLESS;
        settings = requested.withMode(actual);
        frame.setUndecorated(actual != DisplayMode.WINDOWED);
        frame.setResizable(actual == DisplayMode.WINDOWED);
        frame.setExtendedState(JFrame.NORMAL);
        if (actual == DisplayMode.WINDOWED) {
            panel.setPreferredSize(new Dimension(settings.windowWidth(), settings.windowHeight()));
            frame.pack();
            frame.setMinimumSize(new Dimension(500, 400));
            if (windowedLocation == null) frame.setLocationRelativeTo(null);
            else frame.setLocation(windowedLocation);
        } else {
            frame.setMinimumSize(new Dimension(0, 0));
            Rectangle bounds = device.getDefaultConfiguration().getBounds();
            frame.setBounds(bounds);
        }
        frame.setVisible(true);
        if (actual == DisplayMode.FULLSCREEN) {
            try {
                device.setFullScreenWindow(frame);
                fullscreenDevice = device;
            } catch (UnsupportedOperationException e) {
                if (device.getFullScreenWindow() == frame) device.setFullScreenWindow(null);
                frame.setBounds(device.getDefaultConfiguration().getBounds());
                settings = settings.withMode(DisplayMode.BORDERLESS);
            }
        }
        changingMode = false;
        panel.requestFocusInWindow();
        onSettingsChanged.accept(settings);
    }

    private void rememberWindowedSize() {
        if (changingMode || closed || !frame.isVisible() || settings.mode() != DisplayMode.WINDOWED)
            return;
        int width = panel.getWidth(), height = panel.getHeight();
        if (width < 480 || height < 360 || width > 7680 || height > 4320) return;
        windowedLocation = frame.getLocation();
        GameSettings resized = settings.withWindowSize(width, height);
        if (!resized.equals(settings)) {
            settings = resized;
            onSettingsChanged.accept(settings);
        }
    }

    private void releaseFullscreen() {
        if (fullscreenDevice != null && fullscreenDevice.getFullScreenWindow() == frame)
            fullscreenDevice.setFullScreenWindow(null);
        fullscreenDevice = null;
    }

    @Override
    public void close() {
        if (closed) return;
        rememberWindowedSize();
        closed = true;
        resizeSave.stop();
        releaseFullscreen();
        frame.dispose();
    }
}
