package Utility;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Animation {

    private int speed;
    private int frames;
    private int index = 0;
    private int count = 0;

    private BufferedImage[] bImages;

    private BufferedImage currentImg;


    public Animation(int speed, BufferedImage... bImages) {

        this.bImages = bImages;

        this.speed = speed;

        frames = 7;
    }


    public void runAnimation() {
        index++;
        if (index > speed) {
            index = 0;
            nextFrame();
        }

    }

    public void nextFrame() {


        count++;

        if (count > frames)
            count = 0;

    }


    public void drawAnimation(Graphics g, double x, double y, int offset) {
        g.drawImage(bImages[count], (int) x - offset, (int) y, null);


    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;


    }

}