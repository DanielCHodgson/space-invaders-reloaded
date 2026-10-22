package Super;

import Loop.GameLoop;

import java.awt.*;

public class GameObject {

    public int xPosition, yPosition;

    public GameObject(int xPosition, int yPosition) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
    }

    public Rectangle getBounds(int width, int height){
        return new Rectangle(xPosition, yPosition, width, height);
    }

}
