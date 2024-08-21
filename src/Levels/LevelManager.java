package Levels;

import Gamestates.Gamestate;
import Main.Game;
import Utilz.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/*
Manages the games levels
 */
public class LevelManager {
    private Game game;
    private BufferedImage[] levelSprite;
    private ArrayList<Level> levels;
    private int lvlIndex = 0;

    public LevelManager(Game game) {
        this.game = game;
        //levelSprite = LoadSave.GetSpriteAtlas(LoadSave.LEVEL_ATLAS);
        importOutsideSprites();
        levels = new ArrayList<>();
        buildAllLevels();
    }

    public void loadNextLevel(){
        lvlIndex++;
        if(lvlIndex >= levels.size()){ //if completed all levels - game completed overlay?
            lvlIndex = 0; //start from 0
            System.out.println("No more levels! Game Completed");
            Gamestate.state = Gamestate.MENU;
        }
            Level newLevel = levels.get(lvlIndex);
            game.getPlaying().getEnemyManager().loadEnemies(newLevel); //load the enmeies in the new level
            game.getPlaying().getPlayer().loadLvlData(newLevel.getLvlData()); //need to acess player in the level. player needs lvlData for hitboxes, terrain etc
            game.getPlaying().setMaxLvlOffset(newLevel.getMaxLvlOffsetX()); //set current level offset
            game.getPlaying().getObjectManager().loadObjects(newLevel);

    }

    private void buildAllLevels() {
        BufferedImage[] allLevels = LoadSave.GetAllLevels();
        for(BufferedImage img : allLevels){
            levels.add(new Level(img));
        }
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

    public void draw(Graphics g, int lvlOffset) {

        for (int i = 0; i < Game.TILES_IN_HEIGHT; i++){
            for (int j = 0; j < levels.get(lvlIndex).getLvlData()[0].length; j++) { //now drawing entire size of level
                int index = levels.get(lvlIndex).getSpriteIndex(j,i);
                g.drawImage(levelSprite[index], Game.TILES_SIZE*j - lvlOffset, Game.TILES_SIZE*i, Game.TILES_SIZE, Game.TILES_SIZE,  null); //image, x, y, width, height - where to draw the level in the game window
            }
        }
    }

    public void update() {

    }

    public Level getCurrentLevel() {
        return levels.get(lvlIndex);
    }

    public int getAmountOfLevels(){
        return levels.size();
    }

}

