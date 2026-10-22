package Utility;

import Enemies.Alien;
import Enemies.Mothership;
import Loop.GameLoop;
import Player.Lazer;

import java.awt.*;
import java.util.ArrayList;

public class Controller implements DrawableGameObject {

    private ArrayList<Lazer> w = new ArrayList<>();
    private ArrayList<Alien> a = new ArrayList<>();
    private ArrayList<Alien> aTwo = new ArrayList<>();
    private ArrayList<Alien> aThree = new ArrayList<>();

    private ArrayList<Mothership> m = new ArrayList<>();

    Lazer TempLazer;

    Alien TempAlienOne;
    Alien TempAlienTwo;
    Alien TempAlienThree;

    Mothership TempMothership;

    GameLoop gameLoop;

    Textures tex;

    Physics physics;

    public Controller(GameLoop gameLoop, Textures tex) {
        this.gameLoop = gameLoop;
        this.tex = tex;
    }

    public void createMotherShip() {

        // addMotherShip(new Mothership(150, 20, 4, gameLoop, tex, this));

        double xPosition = Math.random() * 500;

        addMotherShip(new Mothership((int) xPosition, 10, 6, gameLoop, tex, this));
    }

    public void createAlienOne() {
        int xPosition = 130;

        for (int i = 0; i < 600; i += 70) {

            addAlien(new Alien(xPosition, 20, 2, gameLoop, tex, this));

            xPosition += 40;
        }
    }

    public void createAlienTwo() {

        int xPosition = 130;

        for (int i = 0; i < 600; i += 70) {

            addAlienTwo(new Alien(xPosition, 60, 2, gameLoop, tex, this));

            xPosition += 40;
        }
    }

    public void createAlienThree() {

        int xPosition = 130;

        for (int i = 0; i < 600; i += 70) {

            addAlienThree(new Alien(xPosition, 100, 2, gameLoop, tex, this));

            xPosition += 40;
        }
    }

    public void moveMothership() {
        m.forEach(m -> m.setxPosition(m.getxPosition() + m.getSpeed()));

        for (int i = 0; i < m.size(); i++) {

            TempMothership = m.get(i);

            if (m.get(i).getxPosition() <= 0 || m.get(i).getxPosition() >= 550) {

                m.forEach(m -> m.setSpeed(-m.getSpeed()));

                m.forEach(m -> m.setyPosition(m.getyPosition() + 15));
                a.forEach(a -> a.setyPosition(a.getyPosition() + 15));
                aTwo.forEach(aTwo -> aTwo.setyPosition(aTwo.getyPosition() + 15));
                aThree.forEach(aThree -> aThree.setyPosition(aThree.getyPosition() + 15));
            }
        }
    }

    public void moveAlienOne() {

        a.forEach(a -> a.setxPosition(a.getxPosition() + a.getSpeed()));

        for (int i = 0; i < a.size(); i++) {

            TempAlienOne = a.get(i);

            if (a.get(i).getxPosition() <= 0 || a.get(i).getxPosition() >= 550) {

                m.forEach(m -> m.setyPosition(m.getyPosition() + 15));
                a.forEach(a -> a.setSpeed(-a.getSpeed()));
                a.forEach(a -> a.setyPosition(a.getyPosition() + 15));
                aTwo.forEach(aTwo -> aTwo.setyPosition(aTwo.getyPosition() + 15));
                aThree.forEach(aThree -> aThree.setyPosition(aThree.getyPosition() + 15));
            }
        }
    }

    public void moveAlienTwo() {

        aTwo.forEach(aTwo -> aTwo.setxPosition(aTwo.getxPosition() - aTwo.getSpeed()));

        for (int i = 0; i < aTwo.size(); i++) {

            TempAlienTwo = aTwo.get(i);

            if (aTwo.get(i).getxPosition() <= 0 || aTwo.get(i).getxPosition() >= 550) {

                aTwo.forEach(aTwo -> aTwo.setSpeed(-aTwo.getSpeed()));
                m.forEach(m -> m.setyPosition(m.getyPosition() + 15));
                a.forEach(a -> a.setyPosition(a.getyPosition() + 15));
                aTwo.forEach(aTwo -> aTwo.setyPosition(aTwo.getyPosition() + 15));
                aThree.forEach(aThree -> aThree.setyPosition(aThree.getyPosition() + 15));
            }
        }
    }

    public void moveAlienThree() {

        aThree.forEach(aThree -> aThree.setxPosition(aThree.getxPosition() + aThree.getSpeed()));

        for (int i = 0; i < aThree.size(); i++) {

            TempAlienThree = aThree.get(i);

            if (aThree.get(i).getxPosition() <= 0 || aThree.get(i).getxPosition() >= 550) {

                aThree.forEach(aThree -> aThree.setSpeed(-aThree.getSpeed()));
                m.forEach(m -> m.setyPosition(m.getyPosition() + 15));
                a.forEach(a -> a.setyPosition(a.getyPosition() + 15));
                aTwo.forEach(aTwo -> aTwo.setyPosition(aTwo.getyPosition() + 15));
                aThree.forEach(aThree -> aThree.setyPosition(aThree.getyPosition() + 15));
            }
        }
    }

    public void tick(double delta) {
        // The controller owns enemy updates as well as drawing. A snapshot lets
        // an enemy remove itself on a hit without changing this iteration.
        new ArrayList<>(a).forEach(alien -> alien.tick(delta));
        new ArrayList<>(aTwo).forEach(alien -> alien.tick(delta));
        new ArrayList<>(aThree).forEach(alien -> alien.tick(delta));
        new ArrayList<>(m).forEach(mothership -> mothership.tick(delta));

        for (int i = 0; i < w.size(); i++) {
            TempLazer = w.get(i);

            if (TempLazer.getyPosition() < 0) {
                removeWeapon(TempLazer);
            }

            TempLazer.tick(delta);
        }

        moveAlienOne();
        moveAlienTwo();
        moveAlienThree();
        moveMothership();
    }

    public void draw(Graphics g) {
        for (int i = 0; i < w.size(); i++) {
            TempLazer = w.get(i);
            TempLazer.draw(g);
        }
        for (int i = 0; i < a.size(); i++) {
            TempAlienOne = a.get(i);
            TempAlienOne.draw(g);
        }
        for (int i = 0; i < aTwo.size(); i++) {
            TempAlienTwo = aTwo.get(i);
            TempAlienTwo.draw(g);
        }

        for (int i = 0; i < aThree.size(); i++) {
            TempAlienThree = aThree.get(i);
            TempAlienThree.draw(g);
        }
        for (int i = 0; i < m.size(); i++) {
            TempMothership = m.get(i);
            TempMothership.draw(g);
        }
    }

    public Rectangle getBounds() {
        return null;
    }

    public void addWeapon(Lazer block) {
        w.add(block);
    }

    public void removeWeapon(Lazer block) {
        w.remove(block);
    }

    public void addMotherShip(Mothership block) {
        m.add(block);
    }

    public void removeMotherShip(Mothership block) {
        m.remove(block);
    }

    public void addAlien(Alien block) {
        a.add(block);
    }

    public void removeAlien(Alien block) {
        a.remove(block);
    }

    public void addAlienTwo(Alien block) {
        aTwo.add(block);
    }

    public void removeAlienTwo(Alien block) {
        aTwo.remove(block);
    }

    public void addAlienThree(Alien block) {
        aThree.add(block);
    }

    public void removeAlienThree(Alien block) {
        aThree.remove(block);
    }

    public ArrayList<Alien> getAlienThree() {
        return aThree;
    }

    public ArrayList<Lazer> getLazer() {
        return w;
    }

    public ArrayList<Mothership> getMothership() {
        return m;
    }

    public ArrayList<Alien> getAlien() {
        return a;
    }

    public ArrayList<Alien> getAlienTwo() {
        return aTwo;
    }

    public Alien getTempAlienOne() {
        return TempAlienOne;
    }

    public Mothership getTempMothership() {
        return TempMothership;
    }

    public void setTempAlienOne(Alien tempAlienOne) {
        TempAlienOne = tempAlienOne;
    }

    public Alien getTempAlienTwo() {
        return TempAlienTwo;
    }

    public void setTempAlienTwo(Alien tempAlienTwo) {
        TempAlienTwo = tempAlienTwo;
    }

    public Alien getTempAlienThree() {
        return TempAlienThree;
    }

    public void setTempAlienThree(Alien tempAlienThree) {
        TempAlienThree = tempAlienThree;
    }
}
