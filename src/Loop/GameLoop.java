package Loop;

import Display.GameWindow;
import Enemies.Alien;
import Enemies.Mothership;
import Player.Lazer;
import Player.Spaceship;
import Sound.Sound;
import Utility.Controller;
import Utility.GameMenu;
import Utility.GameRender;
import Utility.KeyInput;
import Utility.MouseInput;
import Utility.Physics;
import Utility.Textures;

import javax.imageio.ImageIO;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Menu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;

public class GameLoop extends Canvas implements Runnable {

    public boolean running = true;
    private Thread thread;
    private static final String BACKGROUND_PATH = "res\\Space_Background3.png";
    private boolean isShooting = false;
    private Controller c;
    private BufferedImage backgroundImage;

    private int alienOneCount = 0;
    private int alienTwoCount = 0;
    private int alienThreeCount = 0;

    private int enemyKilled = 0;

    public Sound menuMusic;
    public Sound blast;

    long pressTime;
    long Releasetime;

    private GameMenu menu;

    public enum STATE {
        MENU,
        GAME,
        LOSE,
        WIN
    }

    public static STATE State = STATE.MENU;

    public GameLoop() {
        this.backgroundImage = loadImage(BACKGROUND_PATH);
    }

    Textures tex = new Textures();

    Spaceship spaceship = new Spaceship(40, 350, 1, 30, 100, 65, tex);

    GameRender draw = new GameRender();

    GameWindow gameWindow = new GameWindow();

    public List<Lazer> w;

    public List<Alien> a;
    public List<Alien> aTwo;
    public List<Alien> aThree;
    public List<Mothership> m;

    /*public synchronized void start() {
      if (running)
          return;

      running = true;
      run();
    */
    /*  thread = new Thread(this);
    thread.start();*/
    /*

    }*/

    public synchronized void stop() {
        if (!running) return;
        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(1);
    }

    public void initialize() {

        System.out.println("hello");

        menu = new GameMenu();

        c = new Controller(this, tex);

        c.createAlienOne();
        c.createAlienTwo();
        c.createAlienThree();

        // c.createMotherShip();

        a = c.getAlien();
        w = c.getLazer();
        aTwo = c.getAlienTwo();
        aThree = c.getAlienThree();

        m = c.getMothership();

        draw.addDrawableGameComponents(spaceship);
        draw.addDrawableGameComponents(c);

        addKeyListener(new KeyInput(this));

        this.addMouseListener(new MouseInput(this));

        try {

            menuMusic = new Sound("src/Music/Menu_Music.au");

            menuMusic.play();

        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void run() {

        initialize();

        long previous = System.nanoTime();
        final double AMOUNT_OF_TICKS = 60.0;
        double ns = 1000000000 / AMOUNT_OF_TICKS;
        double delta = 0;
        int updates = 0;
        int frames = 0;
        long timer = System.currentTimeMillis();

        while (running) {
            long current = System.nanoTime();
            delta += (current - previous) / ns;
            previous = current;
            if (delta >= 1) {
                tick(delta);
                updates++;
                delta--;
            }
            frames++;

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                // System.out.println(updates + "Ticks,  FPS " + frames);
                updates = 0;
                frames = 0;
            }

            render();
        }

        State = STATE.MENU;
        // stop();
    }

    public void render() {

        BufferStrategy buffer = this.getBufferStrategy();
        Graphics g = buffer.getDrawGraphics();

        if (State == STATE.GAME) {
            draw.getComponents().forEach(c -> c.draw(g));

        } else if (State == STATE.MENU) {

            menu.render(g, gameWindow);

        } else if (State == STATE.LOSE) {
            menu.renderLoseScreen(g, gameWindow);
        }

        buffer.show();
        g.drawImage(
                backgroundImage,
                0,
                0,
                backgroundImage.getWidth(),
                backgroundImage.getHeight(),
                null);
    }

    public void tick(double delta) {

        if (State == STATE.GAME) {
            draw.getComponents().forEach(c -> c.tick(delta));
        }

        scoreConditions();
    }

    public void scoreConditions() {

        alienOneCount = a.size();
        alienTwoCount = aTwo.size();
        alienThreeCount = aThree.size();

        a = c.getAlien();
        aTwo = c.getAlienTwo();
        aThree = c.getAlienThree();

        if (alienThreeCount <= 0 && alienTwoCount <= 0 && alienOneCount <= 0) {

            a = c.getAlien();
            aTwo = c.getAlienTwo();
            aThree = c.getAlienThree();

            c.createAlienOne();
            c.createAlienTwo();
            c.createAlienThree();

        } else if (alienOneCount <= 0) {

            if ((c.getTempAlienTwo().getyPosition() >= 55)
                    && (c.getTempAlienThree().getyPosition() >= 55)) {

                a = c.getAlien();

                c.createAlienOne();
            }

        } else if (alienTwoCount <= 0) {

            if ((c.getTempAlienOne().getyPosition() <= 20
                            || c.getTempAlienOne().getyPosition() >= 100)
                    && (c.getTempAlienThree().getyPosition() <= 20
                            || c.getTempAlienThree().getyPosition() >= 100)) {

                c.createAlienTwo();

                aTwo = c.getAlienTwo();
            }

        } else if (alienThreeCount <= 0) {

            if ((c.getTempAlienTwo().getyPosition() <= 65
                            || c.getTempAlienTwo().getyPosition() >= 135)
                    && (c.getTempAlienOne().getyPosition() <= 65
                            || c.getTempAlienOne().getyPosition() >= 135)) {

                c.createAlienThree();

                aThree = c.getAlienThree();
            }
        }

        if ((getEnemyKilled() % 50 == 0 && getEnemyKilled() != 0) && m.size() == 0) {

            if (c.getTempAlienTwo().getyPosition() >= 55
                    && c.getTempAlienThree().getyPosition() >= 55
                    && c.getTempAlienOne().getyPosition() >= 55) {

                m = c.getMothership();

                c.createMotherShip();
            }
        }
    }

    public void keyPressed(KeyEvent e) {

        if (State == STATE.GAME) {

            int keyCode = e.getKeyCode();

            if ((keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT) && running) {
                spaceship.setVelX(-7);

            } else if ((keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT) && running) {
                spaceship.setVelX(7);

            } else if ((keyCode == KeyEvent.VK_SPACE && running) && !isShooting) {

                try {
                    blast = new Sound("src/Music/Blast.au");
                } catch (UnsupportedAudioFileException b) {
                    b.printStackTrace();
                } catch (IOException b) {
                    b.printStackTrace();
                } catch (LineUnavailableException b) {
                    b.printStackTrace();
                }

                blast.play();

                isShooting = true;

                c.addWeapon(
                        new Lazer(
                                (spaceship.getxPosition() + 12),
                                spaceship.getyPosition(),
                                this,
                                c));
            }
        }
    }

    public void keyReleased(KeyEvent e) {

        int keyCode = e.getKeyCode();

        if ((keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT) && running) {
            spaceship.setVelX(0);

        } else if ((keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT) && running) {
            spaceship.setVelX(0);
        } else if (keyCode == KeyEvent.VK_SPACE) {
            isShooting = false;
        }
    }

    private BufferedImage loadImage(String path) {

        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // GETTERS AND SETTERS

    public int getAlienOneCount() {
        return alienOneCount;
    }

    public void setAlienOneCount(int alienOneCount) {
        this.alienOneCount = alienOneCount;
    }

    public int getAlienTwoCount() {
        return alienTwoCount;
    }

    public void setAlienTwoCount(int alienTwoCount) {
        this.alienTwoCount = alienTwoCount;
    }

    public int getAlienThreeCount() {
        return alienThreeCount;
    }

    public void setAlienThreeCount(int alienThreeCount) {
        this.alienThreeCount = alienThreeCount;
    }

    public int getEnemyKilled() {
        return enemyKilled;
    }

    public void setEnemyKilled(int enemyKilled) {
        this.enemyKilled = enemyKilled;
    }
}
