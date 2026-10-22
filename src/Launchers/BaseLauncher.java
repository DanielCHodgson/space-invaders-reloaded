package Launchers;

import Loop.GameLoop;
import Display.GameWindow;
import java.awt.*;

public class BaseLauncher {

    public static void main(String[] args) {
        Rectangle r = null;
        while (true) {
            r = restartMain(r);
        }
    }


    public static Rectangle restartMain(Rectangle r) {


        GameLoop gameLoop = new GameLoop();
        GameWindow window = new GameWindow();
        window.openGame(gameLoop);
        window.setBounds(r);
        //gameLoop.start();
        Thread t = new Thread(gameLoop);
        t.start();


        while (true) {
            try {
                t.join();
               break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Rectangle rectangle = window.getBounds();
        window.close();
        return  rectangle;
    }
//        gameLoop.start();
}




