package Player;

import Loop.GameLoop;
import Sound.Sound;
import Super.GameObject;
import Utility.Controller;
import Utility.DrawableGameObject;
import Utility.Physics;

import javax.imageio.ImageIO;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Lazer extends GameObject implements DrawableGameObject {

    private static final String SPRITE_PATH = "res\\sprite_sheet.png";
    private BufferedImage spriteSheet;

    private GameLoop gameLoop;

    private boolean collided = false;
    private boolean collidedTwo = false;
    private boolean collidedThree = false;
    private boolean collidedFour = false;

    Sound explosion;

    Controller c;

    public Lazer(int xPosition, int yPosition, GameLoop gameLoop, Controller c) {
        super(xPosition, yPosition);
        this.spriteSheet = loadImage(SPRITE_PATH);
        this.gameLoop = gameLoop;
        this.c = c;
    }

    public void tick(double delta) {

        yPosition -= 10;

        getBounds();

        if (collided || collidedTwo || collidedThree || collidedFour) {
            c.removeWeapon(this);

            try {
                explosion = new Sound("src/Music/boom.au");
            } catch (UnsupportedAudioFileException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            }

            explosion.play();
        }

        collided = Physics.collisionLazer(this, gameLoop.a);
        collidedTwo = Physics.collisionLazer(this, gameLoop.aTwo);
        collidedThree = Physics.collisionLazer(this, gameLoop.aThree);
        collidedFour = Physics.collisionLazerMothership(this, gameLoop.m);
    }

    public void draw(Graphics g) {
        g.drawImage(extractImage(spriteSheet, 0, 2), xPosition, yPosition, null);
    }

    private BufferedImage loadImage(String path) {

        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public BufferedImage extractImage(BufferedImage image, int row, int col) {
        return image.getSubimage(col * 32, row * 32, 32, 32);
    }

    public Rectangle getBounds() {

        return new Rectangle(xPosition, yPosition, 8, 16);
    }

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
}
