package Levels;

import Main.Game;
import Utilz.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;


public class LevelManager {
    private Game game;
    private BufferedImage[] levelSprite;
    private Level levelOne;

    public LevelManager(Game game) {
        this.game = game;
        //levelSprite = LoadSave.GetSpriteAtlas(LoadSave.LEVEL_ATLAS);
        importOutsideSprites();
        levelOne = new Level(LoadSave.GetLevelData());
    }

    private void importOutsideSprites() {
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.LEVEL_ATLAS);
        levelSprite = new BufferedImage[48]; //12 sprites wide 4 sprites in height
        for (int i = 0; i < 4; i++){ //i is height, j is alongways.
            for (int j = 0; j < 12; j++){
                int index = i*12 + j; //this will get each block in a row
                levelSprite[index] = img.getSubimage(j*32, i*32, 32, 32);
            }
        }
    }

    public void draw(Graphics g) {

        for (int i = 0; i < Game.TILES_IN_HEIGHT; i++){
            for (int j = 0; j < Game.TILES_IN_WIDTH; j++) {
                int index = levelOne.getSpriteIndex(j,i);
                g.drawImage(levelSprite[index], Game.TILES_SIZE*j, Game.TILES_SIZE*i, Game.TILES_SIZE, Game.TILES_SIZE,  null); //image, x, y, width, height - where to draw the level in the game window
            }
        }
    }

    public void update() {

    }

}
