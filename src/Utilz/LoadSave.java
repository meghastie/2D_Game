package Utilz;

import Main.Game;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class LoadSave {
    public static final String PLAYER_ATLAS = "player_sprites.png";
    public static final String LEVEL_ATLAS = "outside_sprites.png";
    public static final String LEVEL_ONE_DATA = "level_one_data.png";

    public static BufferedImage GetSpriteAtlas(String fileName) {
        BufferedImage img = null;
        InputStream is = LoadSave.class.getResourceAsStream("/" + fileName); //import the image from res, the / tells the game that the image can be found IN one of the folders, not next to it
        try {
            img = ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return img;
    }

    public static int[][] GetLevelData(){
        int[][] lvlData = new int[Game.TILES_IN_HEIGHT][Game.TILES_IN_WIDTH];
        BufferedImage img = GetSpriteAtlas(LEVEL_ONE_DATA);

        for (int i = 0; i < img.getHeight(); i++){
            for (int j = 0; j < img.getWidth(); j++){
                Color color = new Color(img.getRGB(j,i));
                int value = color.getRed(); //whatever value red is will be index later for that sprite
                if(value >= 48) { //if greater than 48 that index does not exist in sprite atlas (think outsides_sprites.png screenshot, 4 height x 12 width = 48)
                    value = 0;
                }
                lvlData[i][j] = value;
            }
        }
        return lvlData;
    }
}
