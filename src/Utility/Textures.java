package Utility;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Textures {

    public BufferedImage[] spaceship = new BufferedImage[8];
    public BufferedImage[] alien = new BufferedImage[8];
    public BufferedImage[] bossShip = new BufferedImage[8];
    public BufferedImage[] Lazer = new BufferedImage[6];

    private static final String SPRITE_PATH = "res\\sprite_sheet.png";
    private BufferedImage spriteSheet;

    public Textures() {
        this.spriteSheet = loadImage(SPRITE_PATH);

        getTextures();
    }

    public void getTextures() {

        int row = 0;
        for (int i = 0; i < 8; i++) {

            spaceship[i] = extractImage(spriteSheet, row, 0);
            alien[i] = extractImage(spriteSheet, row, 1);
            bossShip[i] = extractBossImage(spriteSheet, row, 3);

            row++;
        }
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

    public BufferedImage extractBossImage(BufferedImage image, int row, int col) {
        return image.getSubimage(col * 32, row * 32, 64, 32);
    }
}
