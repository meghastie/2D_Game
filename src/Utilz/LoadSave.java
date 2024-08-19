package Utilz;

import Entities.Crabby;
import Main.Game;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static Utilz.Constants.EnemyConstants.CRABBY;

/*
Gets our inputs, like the levels and sprites.
 */
public class LoadSave {
    public static final String PLAYER_ATLAS = "player_sprites.png";
    public static final String LEVEL_ATLAS = "outside_sprites.png";
    //public static final String LEVEL_ONE_DATA = "level_one_data.png";
    public static final String LEVEL_ONE_DATA = "level_one_data_long.png";
    public static final String MENU_BUTTONS = "button_atlas.png";
    public static final String MENU_BACKGROUND = "menu_background.png";
    public static final String PAUSE_BACKGROUND = "pause_menu.png";
    public static final String SOUND_BUTTONS = "sound_button.png";
    public static final String URM_BUTTONS = "urm_buttons.png"; //unpause,REPLAY,menu
    public static final String VOLUME_BUTTONS = "volume_buttons.png";
    public static final String MENU_BACKGROUND_IMG = "background_menu.png";
    public static final String PLAYING_BG_IMG = "playing_bg_img.png";
    public static final String BIG_CLOUDS = "big_clouds.png";
    public static final String SMALL_CLOUDS = "small_clouds.png";
    public static final String CRABBY_SPRITE = "crabby_sprite.png";

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


    /*
    he code takes a picture where each dot represents a part of your game world.
    It checks how red each dot is, uses that information to decide what type of tile belongs there (e.g. grass, water, lava...), and then builds a level for your game based on that picture.
    Think of your game level as an image where each tiny dot (pixel) in the picture represents a single square (tile) in your game.
    The color of the dot (let’s say how red it is) tells the computer what type of tile goes there
    The code looks at each dot (pixel) in the image and checks how red it is. It then makes a note in a big grid (a 2D array) about what kind of tile each square should be based on how red the pixel is.
     */
    public static int[][] GetLevelData(){

        BufferedImage img = GetSpriteAtlas(LEVEL_ONE_DATA);
        int[][] lvlData = new int[img.getHeight()][img.getWidth()]; //lvl data will be same size as acual whole leve - not just visible tiles
        //This array will eventually hold the indices for the sprites that represent different tiles in the game level

        for (int i = 0; i < img.getHeight(); i++){ //The nested for loops are used to traverse every pixel in the image:
            for (int j = 0; j < img.getWidth(); j++){ //i represents the current row (height or y-axis) and j represents the current column (width or x-axis). This means the loops are iterating over each pixel in the image from top to bottom and left to right.
                Color color = new Color(img.getRGB(j,i)); //For each pixel at position (j, i), the code retrieves the RGB color value of that pixel using img.getRGB(j, i).
                int value = color.getRed(); //whatever value red is will be index later for that sprite - This red value will be used to determine what kind of tile or sprite should be placed at that location in the level
                if(value >= 48) { //if greater than 48 that index does not exist in sprite atlas (think outsides_sprites.png screenshot, 4 height x 12 width = 48)
                    value = 0;
                }
                lvlData[i][j] = value;
            }
        }
        return lvlData;
    }

    /*
    Go over image. if we find colour where green va.lue = crabby (0), then we add a crabby at that pos to the list the return the list.
     */
    public static ArrayList<Crabby> GetCrabs(){
        BufferedImage img = GetSpriteAtlas(LEVEL_ONE_DATA);
        ArrayList<Crabby> list = new ArrayList<>();

        for (int i = 0; i < img.getHeight(); i++){
            for (int j = 0; j < img.getWidth(); j++){
                Color color = new Color(img.getRGB(j,i));
                int value = color.getGreen();
                if(value == CRABBY) {
                    list.add(new Crabby(j * Game.TILES_SIZE, i * Game.TILES_SIZE));
                }
            }
        }
        return list;
    }
}
