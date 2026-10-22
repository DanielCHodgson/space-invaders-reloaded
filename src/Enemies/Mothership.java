package Enemies;

import Loop.GameLoop;
import Player.Lazer;
import Utility.Animation;
import Utility.Controller;
import Utility.DrawableGameObject;
import Super.GameObject;
import Utility.GameMenu;
import Utility.Physics;
import Utility.Textures;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLOutput;

public class Mothership extends GameObject implements DrawableGameObject {

    private static final String SPRITE_PATH = "res\\sprite_sheet.png";
    private BufferedImage spriteSheet;


    private int speed;


    private Controller c;

    private Textures tex;

    private GameLoop gameLoop;

    private int health = 3;
    private int hit;


    Animation animAlienBoss;
    GameMenu menu;


    public Mothership(int xPosition, int yPosition, int speed, GameLoop gameLoop, Textures tex, Controller c) {


        super(xPosition, yPosition);


        this.spriteSheet = loadImage(SPRITE_PATH);

        this.speed = speed;

        this.gameLoop = gameLoop;

        this.tex = tex;

        this.c = c;

        animAlienBoss = new Animation(10, tex.bossShip[0], tex.bossShip[1], tex.bossShip[2], tex.bossShip[3], tex.bossShip[4], tex.bossShip[5], tex.bossShip[6], tex.bossShip[7]);


    }


    public void draw(Graphics g) {

        animAlienBoss.drawAnimation(g, xPosition, yPosition, 0);
        //g.drawImage(extractImage(spriteSheet, 0, 3), xPosition, yPosition, null);




    }

    public void tick(double delta) {

        animAlienBoss.runAnimation();


        getBounds();


        if (Physics.collision(this, gameLoop.w)) {

            health--;
            hit++;


        }

        if (health == 0) {

            setxPosition(-100);
            c.removeMotherShip(this);



        }

        if (hit == 3) {
            gameLoop.setEnemyKilled(gameLoop.getEnemyKilled() + 20);
            hit = 0;
        }




        if (yPosition >= 350) {

            gameLoop.State = GameLoop.STATE.LOSE;

        }



    }


    public BufferedImage extractImage(BufferedImage image, int row, int col) {
        return image.getSubimage(col * 32, row * 32, 64, 32);

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
        return new Rectangle(xPosition, yPosition, 64, 32);
    }


    //Getters and Setters

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
