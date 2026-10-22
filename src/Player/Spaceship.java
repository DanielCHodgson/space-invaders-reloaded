package Player;

import Super.GameObject;
import Utility.Animation;
import Utility.DrawableGameObject;
import Utility.Textures;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Spaceship extends GameObject implements DrawableGameObject {
    private static final String SPRITE_PATH = "res\\sprite_sheet.png";
    private BufferedImage spriteSheet;

    private int speed;
    private int scale;
    private int width;
    private int height;
    private double velX = 0;
    private Textures tex;

    Animation animSpaceship;

    public Spaceship(
            int xPosition,
            int yPosition,
            int speed,
            int scale,
            int width,
            int height,
            Textures tex) {

        super(xPosition, yPosition);
        this.speed = speed;
        this.scale = scale;
        this.width = width;
        this.height = height;
        this.spriteSheet = loadImage(SPRITE_PATH);

        this.tex = tex;

        animSpaceship = new Animation(6, tex.spaceship);
    }

    public void draw(Graphics g) {

        animSpaceship.drawAnimation(g, xPosition, yPosition, 0);
    }

    public void tick(double delta) {
        xPosition += velX;

        if (xPosition <= 5) xPosition = 5;
        if (xPosition >= 550) xPosition = 550;

        animSpaceship.runAnimation();
    }

    public Rectangle getBounds() {
        return new Rectangle(xPosition, yPosition, 32, 32);
    }

    private BufferedImage loadImage(String path) {

        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public double getVelX() {
        return velX;
    }

    public void setVelX(double velX) {
        this.velX = velX;
    }
}
