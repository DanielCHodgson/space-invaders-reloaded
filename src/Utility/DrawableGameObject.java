package Utility;

import java.awt.*;

public interface DrawableGameObject {
    void draw(Graphics g);

    void tick(double delta);

    Rectangle getBounds();
}
