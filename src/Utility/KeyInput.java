package Utility;

import Loop.GameLoop;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyInput extends KeyAdapter {

    GameLoop gameLoop;

    public KeyInput(GameLoop gameLoop) {
        this.gameLoop = gameLoop;
    }

    public void keyPressed(KeyEvent e) {
        gameLoop.keyPressed(e);
    }

    public void keyReleased(KeyEvent e) {
        gameLoop.keyReleased(e);
    }
}
