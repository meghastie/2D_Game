package Levels;

import Entities.Crabby;
import Main.Game;
import Objects.GameContainer;
import Objects.Potion;
import Utilz.HelpMethods;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static Utilz.HelpMethods.*;

public class Level {

    /*
    lvlData is a 2D array of integers (int[][]). Each element in this array corresponds to a specific tile or block in the game world. The first index represents the row (y-axis), and the second index represents the column (x-axis). This forms a grid, with each cell in the grid representing a piece of the level.
     Each value in the lvlData array represents an index that corresponds to a specific sprite or tile in a sprite sheet. For example, if lvlData[y][x] = 3, it could mean that the tile at position (x, y) in the level should be drawn using the sprite located at index 3 in the sprite sheet.
     */
    private int[][] lvlData;
    private BufferedImage img;
    private int lvlTilesWide;//HOW MANY TILES WIDE THE WHOLEELEVEL IS - getting image width with .length . used to calulate max offset
    private int maxTilesOffset; ////need a max val offset can be - dont want to move screen if there is nothing to move to.
    private int maxLvlOffsetX; //turn maxTilesOffset into pixels
    private ArrayList<Crabby> crabs;
    private Point playerSpawn;
    private ArrayList<Potion> potions;
    private ArrayList<GameContainer> containers;

    public Level(BufferedImage img) {
        this.img = img;
        createLevelData();
        createEnemies();
        createPotions();
        createContainers();
        calcLvlOffsets();
        calcPlayerSpawn();
    }

    private void createContainers() {
        containers = HelpMethods.GetContainers(img);
    }

    private void createPotions() {
        potions = HelpMethods.GetPotions(img);
    }

    public void calcPlayerSpawn(){
        playerSpawn = GetPlayerSpawn(img);
    }

    private void calcLvlOffsets() {
        lvlTilesWide = img.getWidth();
        maxTilesOffset = lvlTilesWide - Game.TILES_IN_WIDTH;
        maxLvlOffsetX = Game.TILES_SIZE * maxTilesOffset;
    }

    private void createEnemies() {
        crabs = GetCrabs(img);
    }

    private void createLevelData() {
        lvlData = GetLevelData(img);
    }

    public int getSpriteIndex(int x, int y){
        return lvlData[y][x]; //y-axis, x-axis
        //the value returned is the index of the sprite that should be rendered at that specific position in the level. By using this method, the game can determine which sprite to draw based on the current position within the level grid
    }

    public int[][] getLvlData(){
        return lvlData;
    }

    public int getMaxLvlOffsetX(){
        return maxLvlOffsetX;
    }

    public ArrayList<Crabby> getCrabs() {
        return crabs;
    }

    public Point getPlayerSpawn() {
        return playerSpawn;
    }

    public ArrayList<Potion> getPotions(){
        return potions;
    }

    public ArrayList<GameContainer> getContainers(){
        return containers;
    }


}
