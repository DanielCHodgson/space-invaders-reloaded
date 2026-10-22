package Utility;

import Loop.GameLoop;
import Sound.Sound;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.sql.SQLOutput;

public class MouseInput implements MouseListener {

    GameLoop gameLoop;
    Sound gameMusic;

    public MouseInput(GameLoop gameLoop) {
        this.gameLoop = gameLoop;
    }

    public void mouseClicked(MouseEvent e) {}

    public void mousePressed(MouseEvent e) {

        int mX = e.getX();
        int mY = e.getY();

        /*  public Rectangle playButton = new Rectangle(225,  150, 150, 50);
        public Rectangle helpButton = new Rectangle(225,  225, 150, 50);
        public Rectangle quitButton = new Rectangle(225,  300, 150, 50);
        */

        // Play Button
        if (((mX >= 225 && mX <= 375)
                && (mY >= 150 && mY <= 200)
                && GameLoop.State == GameLoop.STATE.MENU)) {

            // Pressed Play Button
            GameLoop.State = GameLoop.STATE.GAME;

            try {
                gameLoop.menuMusic.stop();
            } catch (UnsupportedAudioFileException g) {
                g.printStackTrace();
            } catch (IOException g) {
                g.printStackTrace();
            } catch (LineUnavailableException g) {
                g.printStackTrace();
            }

            try {
                gameMusic = new Sound("src/Music/Game_Music.au");
            } catch (UnsupportedAudioFileException g) {
                g.printStackTrace();
            } catch (IOException g) {
                g.printStackTrace();
            } catch (LineUnavailableException g) {
                g.printStackTrace();
            }

            gameMusic.play();
        }

        if (((mX >= 225 && mX <= 375)
                && (mY >= 150 && mY <= 200)
                && GameLoop.State == GameLoop.STATE.LOSE)) {

            // Pressed Retry Button
            try {
                gameMusic.stop();
            } catch (UnsupportedAudioFileException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (LineUnavailableException e1) {
                e1.printStackTrace();
            }

            gameLoop.running = false;
            gameLoop.run();
        }

        // Quit Button
        if (((mX >= 225 && mX <= 375)
                && (mY >= 300 && mY <= 350)
                && (GameLoop.State == GameLoop.STATE.MENU
                        || GameLoop.State == GameLoop.STATE.LOSE))) {

            // Pressed Quit Button

            System.exit(1);
        }
    }

    public void mouseReleased(MouseEvent e) {}

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}
}
