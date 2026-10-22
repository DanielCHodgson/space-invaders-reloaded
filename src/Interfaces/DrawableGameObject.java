package Interfaces;

import java.awt.*;

public interface DrawableGameObject {
    void draw(Graphics g);
    void tick();

    Rectangle getBounds();


}
