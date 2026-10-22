package invaders.ui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

/** Native window lifecycle, kept out of the simulation and renderer. */
public final class GameWindow implements AutoCloseable {
    private final JFrame frame = new JFrame("Space Invaders");

    public GameWindow(GamePanel panel, Runnable onClose) {
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent event) {
                        onClose.run();
                    }
                });
        frame.setResizable(false);
        frame.pack();
        frame.setLocationByPlatform(true);
    }

    public void open() {
        frame.setVisible(true);
        frame.getContentPane().requestFocusInWindow();
    }

    @Override
    public void close() {
        frame.dispose();
    }
}
