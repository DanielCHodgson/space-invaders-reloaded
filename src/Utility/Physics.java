package Utility;

import Enemies.Alien;
import Enemies.Mothership;
import Player.Lazer;
import Super.GameObject;

import java.util.List;

public class Physics {

    public static boolean collision(GameObject go, List<Lazer> lazers) {

        for (int i = 0; i < lazers.size(); i++) {

            if (go.getBounds(32, 32).intersects(lazers.get(i).getBounds())) {
                return true;
            }
        }

        return false;
    }

    public static boolean collisionLazer(GameObject go, List<Alien> aliens) {

        for (int i = 0; i < aliens.size(); i++) {

            if (go.getBounds(32, 32).intersects(aliens.get(i).getBounds())) {
                return true;
            }
        }

        return false;
    }

    public static boolean collisionLazerMothership(GameObject go, List<Mothership> mothership) {

        for (int i = 0; i < mothership.size(); i++) {

            if (go.getBounds(32, 32).intersects(mothership.get(i).getBounds())) {
                return true;
            }
        }

        return false;
    }
}
