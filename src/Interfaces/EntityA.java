package Interfaces;

import java.awt.Graphics;
import java.awt.Rectangle;

public interface EntityA {

    void tick();

    void draw(Graphics g);

    int getxPosition();

    int getyPosition();

    Rectangle getBounds();
}
