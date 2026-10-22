package Utility;

import Display.GameWindow;
import Loop.GameLoop;


import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.Font;


public class GameMenu {


    public Rectangle playButton;
    public Rectangle helpButton;
    public Rectangle quitButton;

    public void render(Graphics g, GameWindow gameWindow) {


      Rectangle playButton = new Rectangle(225,  150, 150, 50);
      Rectangle helpButton = new Rectangle(225,  225, 150, 50);
      Rectangle quitButton = new Rectangle(225,  300, 150, 50);

        Graphics2D g2d = (Graphics2D) g;

        Font fnt0 = new Font("arial", Font.BOLD, 50);
        g.setFont(fnt0);
        g.setColor(Color.WHITE);
        g.drawString("SPACE INVADERS", gameWindow.getWIDTH() / 4, 100);

        Font fnt1 = new Font("arial", Font.BOLD, 30);
        g.setFont(fnt1);
        g.drawString("Play", playButton.x + 45, playButton.y + 35);
        g2d.draw(playButton);
        g.drawString("Help", helpButton.x + 42, helpButton.y + 35);
        g2d.draw(helpButton);
        g.drawString("Quit", quitButton.x + 45, quitButton.y + 35);
        g2d.draw(quitButton);
    }


    public void renderLoseScreen(Graphics g, GameWindow gameWindow) {

        Rectangle playButton = new Rectangle(225,  150, 150, 50);
        Rectangle helpButton = new Rectangle(225,  225, 150, 50);
        Rectangle quitButton = new Rectangle(225,  300, 150, 50);

        Graphics2D g2d = (Graphics2D) g;

        Font fnt0 = new Font("arial", Font.BOLD, 50);
        g.setFont(fnt0);
        g.setColor(Color.WHITE);
        g.drawString("GAME OVER", gameWindow.getWIDTH() / 2, 100);

        Font fnt1 = new Font("arial", Font.BOLD, 30);
        g.setFont(fnt1);
        g.drawString("Retry", playButton.x + 40, playButton.y + 35);
        g2d.draw(playButton);
        g.drawString("Help", helpButton.x + 42, helpButton.y + 35);
        g2d.draw(helpButton);
        g.drawString("Rage Quit", quitButton.x + 5, quitButton.y + 35);
        g2d.draw(quitButton);

    }
}
