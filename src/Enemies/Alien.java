package Enemies;

import Loop.GameLoop;
import Player.Lazer;
import Sound.Sound;
import Utility.Animation;
import Utility.Controller;
import Utility.DrawableGameObject;
import Super.GameObject;
import Utility.GameMenu;
import Utility.Physics;
import Utility.Textures;

import javax.imageio.ImageIO;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLOutput;

public class Alien extends GameObject implements DrawableGameObject {

    private static final String SPRITE_PATH = "res\\sprite_sheet.png";
    private BufferedImage spriteSheet;

    private int speed;

    private Controller c;

    private Textures tex;

    private GameLoop gameLoop;

    private boolean collided = false;

    Animation animAlien;
    GameMenu menu;

    Sound explosion;

    public Alien(
            int xPosition,
            int yPosition,
            int speed,
            GameLoop gameLoop,
            Textures tex,
            Controller c) {

        super(xPosition, yPosition);

        this.spriteSheet = loadImage(SPRITE_PATH);

        this.speed = speed;

        this.gameLoop = gameLoop;

        this.tex = tex;

        this.c = c;

        animAlien =
                new Animation(
                        10,
                        tex.alien[0],
                        tex.alien[1],
                        tex.alien[2],
                        tex.alien[3],
                        tex.alien[4],
                        tex.alien[5],
                        tex.alien[6],
                        tex.alien[7]);
    }

    public void draw(Graphics g) {

        animAlien.drawAnimation(g, xPosition, yPosition, 0);

        Font fnt0 = new Font("arial", Font.BOLD, 15);
        g.setFont(fnt0);
        g.setColor(Color.WHITE);
        g.drawString("Score: " + gameLoop.getEnemyKilled(), 25, 25);
    }

    public void tick(double delta) {

        animAlien.runAnimation();

        getBounds();

        if (Physics.collision(this, gameLoop.w)) {
            c.removeAlien(this);
            c.removeAlienTwo(this);
            c.removeAlienThree(this);

            setxPosition(-100);

            /*try {
                explosion = new Sound("src/Music/boom.au");
            } catch (UnsupportedAudioFileException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            }

            explosion.play();*/

            gameLoop.setEnemyKilled(gameLoop.getEnemyKilled() + 1);
        }

        if (yPosition >= 350) {

            gameLoop.State = GameLoop.STATE.LOSE;
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

    public Rectangle getBounds() {
        return new Rectangle(xPosition, yPosition, 32, 32);
    }

    // Getters and Setters

    public int getxPosition() {
        return xPosition;
    }

    public void setxPosition(int xPosition) {
        this.xPosition = xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    public void setyPosition(int yPosition) {
        this.yPosition = yPosition;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
