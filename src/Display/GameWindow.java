package Display;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GameWindow {

    private Frame frame;

    final int WIDTH = 300;
    final int HEIGHT = WIDTH / 12 * 9;
    final int SCALE = 2;
    final String TITLE = "Space Invaders";

    public Window openGame(Canvas canvas) {

        Frame frame = new Frame();
        frame.add(canvas);
        frame.setTitle(TITLE);
        frame.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        frame.setMaximumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        frame.setMinimumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setIgnoreRepaint(true);
        frame.addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        super.windowClosing(e);
                        System.exit(0);
                    }
                });

        frame.pack();
        this.frame = frame;
        canvas.createBufferStrategy(2);
        canvas.requestFocusInWindow();
        return frame;
    }

    public Rectangle getBounds() {
        return frame.getBounds();
    }

    public void setBounds(Rectangle r) {
        if (r != null) frame.setBounds(r);
    }

    public void close() {
        frame.dispose();
    }

    public int getWIDTH() {
        return WIDTH;
    }

    public int getHEIGHT() {
        return HEIGHT;
    }

    public int getSCALE() {
        return SCALE;
    }

    public String getTITLE() {
        return TITLE;
    }
}
