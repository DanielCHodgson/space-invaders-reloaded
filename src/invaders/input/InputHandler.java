package invaders.input;

import invaders.control.GameControls;
import invaders.control.GameKey;
import invaders.render.Viewport;
import invaders.scene.PointerInput;

import java.awt.Component;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Maps physical keyboard aliases and window coordinates into platform-independent game commands.
 */
public final class InputHandler extends KeyAdapter implements FocusListener, MouseListener {
    private final GameControls keys;
    private final PointerInput pointer;
    private final Component surface;
    private final Set<Integer> held = new HashSet<>();

    public InputHandler(GameControls keys, PointerInput pointer, Component surface) {
        this.keys = keys;
        this.pointer = pointer;
        this.surface = surface;
    }

    public void attach() {
        surface.addKeyListener(this);
        surface.addFocusListener(this);
        surface.addMouseListener(this);
    }

    public void detach() {
        clear();
        surface.removeKeyListener(this);
        surface.removeFocusListener(this);
        surface.removeMouseListener(this);
    }

    private GameKey map(int code) {
        return switch (code) {
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> GameKey.LEFT;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> GameKey.RIGHT;
            case KeyEvent.VK_SPACE -> GameKey.FIRE;
            default -> null;
        };
    }

    @Override
    public void keyPressed(KeyEvent event) {
        GameKey key = map(event.getKeyCode());
        if (key != null && held.add(event.getKeyCode())) keys.press(key);
    }

    @Override
    public void keyReleased(KeyEvent event) {
        GameKey key = map(event.getKeyCode());
        held.remove(event.getKeyCode());
        if (key != null && held.stream().noneMatch(code -> map(code) == key)) keys.release(key);
    }

    private void clear() {
        held.clear();
        keys.clearInput();
    }

    @Override
    public void focusLost(FocusEvent event) {
        clear();
    }

    @Override
    public void focusGained(FocusEvent event) {}

    @Override
    public void mousePressed(MouseEvent event) {
        if (event.getButton() != MouseEvent.BUTTON1) return;
        Viewport.fit(surface.getWidth(), surface.getHeight())
                .toGame(event.getX(), event.getY())
                .ifPresent(point -> pointer.click(point.x, point.y));
        surface.requestFocusInWindow();
    }

    @Override
    public void mouseReleased(MouseEvent event) {}

    @Override
    public void mouseClicked(MouseEvent event) {}

    @Override
    public void mouseEntered(MouseEvent event) {}

    @Override
    public void mouseExited(MouseEvent event) {}
}
