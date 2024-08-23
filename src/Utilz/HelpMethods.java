package Utilz;

import Entities.Crabby;
import Main.Game;
import Objects.Cannon;
import Objects.GameContainer;
import Objects.Potion;
import Objects.Spike;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static Utilz.Constants.EnemyConstants.CRABBY;
import static Utilz.Constants.ObjectConstants.*;

public class HelpMethods {

    /*
    check if pos overlaps any tiles
     */
    public static Boolean CanMoveHere(float x, float y, float width, float height, int[][] lvlData) { //pass in width and height of hitbox, not sprite
        if (!IsSolid(x, y, lvlData)) //top left
            if (!IsSolid(x + width, y + height, lvlData)) //bottom right
                if (!IsSolid(x + width, y, lvlData)) //top right
                    if (!IsSolid(x, y + height, lvlData)) //bottom left
                        return true;
        return false;
    }

    /*
    check it is a tile but also check it is within the game window
     */
    private static Boolean IsSolid(float x, float y, int[][] lvlData) {
        int maxWidth = lvlData[0].length * Game.TILES_SIZE; //width in pixels of level times the size of a tile - the actual max width of the whole level, notjust visible tiles
        if (x < 0 | x >= maxWidth) { //if withinh these bounds, it is ok - can check wether we have a walkable tile
            return true; //it is solid - dont go here
        }
        if (y < 0 || y >= Game.GAME_HEIGHT) {
            return true;
        }
        //if none fails, we are inside game window, so have to check where we are (where in lvlData)

        float xIndex = x / Game.TILES_SIZE;
        float yIndex = y / Game.TILES_SIZE;

        return IsTileSolid((int) xIndex, (int) yIndex, lvlData);
    }

    public static Boolean IsTileSolid(int xTile, int yTile, int[][] lvlData) {
        int value = lvlData[yTile][xTile]; //where we are in level

        if (value >= 48 || value < 0 || value != 11) { //check if value is a tile - 48 sprites, 48 and up will be no tiles at all. below 0 is not a tile. 11th sprite is transparent, so can pass throught(see lvlatlas) e.g. not solid
            return true;
        } else {
            return false;
        }
    }

    public static float GetEntityXPosNextToWall(Rectangle2D.Float hitbox, float xSpeed) {
        int currentTile = (int) (hitbox.x / Game.TILES_SIZE); //current tile player is on. will iether collide on tile to left which is one less, or tile to the right which is one more

        if (xSpeed > 0) { //wall is on right
            int tileXPos = currentTile * Game.TILES_SIZE;
            int xOffset = (int) (Game.TILES_SIZE - hitbox.width); //diff between size of tile and size of player
            return tileXPos + xOffset - 1;
        } else { //wall is on left
            return currentTile * Game.TILES_SIZE;
        }
    }

    public static float GetEntityYPosUnderRoofOrAboveFloor(Rectangle2D.Float hitbox, Float airSpeed) {
        int currentTile = (int) (hitbox.y / Game.TILES_SIZE); //tile will either be right above or below. as htibox is smaller than any tiles, can only be either 1 tile up or down

        if (airSpeed > 0) { //Falling/touching floor
            int tileYPos = currentTile * Game.TILES_SIZE;
            int yOffset = (int) (Game.TILES_SIZE - hitbox.height);
            return tileYPos + yOffset - 1;
        } else { //Jumping
            return currentTile * Game.TILES_SIZE;
        }
    }

    public static boolean IsEntityOnFloor(Rectangle2D.Float hitbox, int[][] lvlData) { //check if entity is on floor - before had bug where entity could walk in air
        //check pixel below bottomleft and bottomright. if not solid, in air

        if (!IsSolid(hitbox.x, hitbox.y + hitbox.height, lvlData)) { //bottom left corner
            if (!IsSolid(hitbox.x + hitbox.width, hitbox.y + hitbox.height, lvlData)) { //bottom right
                return false;
            }
        }
        return true;
    }

    public static boolean IsFloor(Rectangle2D.Float hitbox, float xSpeed, int[][] lvlData) {
        if(xSpeed > 0){
            return IsSolid(hitbox.x + hitbox.width + xSpeed, hitbox.y + hitbox.height + 1, lvlData);
        }else {
            return IsSolid(hitbox.x + xSpeed, hitbox.y + hitbox.height + 1, lvlData); //if its solid we can walk ther. hitbox.y + hitbox.height + 1 - not checking hitbox, checking pixel below
        }
    }

    public static boolean CanCannonSeePlayer(int[][] lvlData, Rectangle2D.Float firstHitbox, Rectangle2D.Float secondHitbox, int yTile){
        int firstXTile = (int) (firstHitbox.x / Game.TILES_SIZE);
        int secondXTile = (int) (secondHitbox.x / Game.TILES_SIZE);

        if (firstXTile > secondXTile) {
            return IsAllTilesClear(secondXTile, firstXTile, yTile, lvlData);
        } else {
            return IsAllTilesClear(firstXTile, secondXTile, yTile, lvlData);
        }
    }

    //checks tiles. just need tiles from x and y for cannon
    public static boolean IsAllTilesClear(int xStart, int xEnd, int y, int[][] lvlData){
        for (int i = 0; i < xEnd - xStart; i++) { //i < diff between tiles
            if (IsTileSolid(xStart + i, y, lvlData)) { // start with second as second tile is smaller than first
                return false;
            }
        }
        return true;
    }

    //checks tiles beneath tiles we are checking - needed for enemy
    public static boolean IsAllTilesWalkable(int xStart, int xEnd, int y, int[][] lvlData) {
        if(IsAllTilesClear(xStart, xEnd, y, lvlData)){
            for (int i = 0; i < xEnd - xStart; i++) { //i < diff between tiles
                if (!IsTileSolid(xStart + i, y + 1, lvlData)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean IsSightClear(int[][] lvlData, Rectangle2D.Float firstHitbox, Rectangle2D.Float secondHitbox, int yTile) { //in helper methods as may be used for things like projectiles etc. other classes will use apart form enemy
        int firstXTile = (int) (firstHitbox.x / Game.TILES_SIZE);
        int secondXTile = (int) (secondHitbox.x / Game.TILES_SIZE);

        if (firstXTile > secondXTile) {
            return IsAllTilesWalkable(secondXTile, firstXTile, yTile, lvlData);
        } else {
            return IsAllTilesWalkable(firstXTile, secondXTile, yTile, lvlData);
        }
    }

    /*
    he code takes a picture where each dot represents a part of your game world.
    It checks how red each dot is, uses that information to decide what type of tile belongs there (e.g. grass, water, lava...), and then builds a level for your game based on that picture.
    Think of your game level as an image where each tiny dot (pixel) in the picture represents a single square (tile) in your game.
    The color of the dot (let’s say how red it is) tells the computer what type of tile goes there
    The code looks at each dot (pixel) in the image and checks how red it is. It then makes a note in a big grid (a 2D array) about what kind of tile each square should be based on how red the pixel is.
     */
    public static int[][] GetLevelData(BufferedImage img){
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
    public static ArrayList<Crabby> GetCrabs(BufferedImage img){
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

    public static Point GetPlayerSpawn(BufferedImage img){
        for (int i = 0; i < img.getHeight(); i++){
            for (int j = 0; j < img.getWidth(); j++){
                Color color = new Color(img.getRGB(j,i));
                int value = color.getGreen();
                if(value == 100) {
                    return new Point(j * Game.TILES_SIZE, i * Game.TILES_SIZE);
                }
            }
        }
        return new Point(1 * Game.TILES_SIZE, 1 * Game.TILES_SIZE);
    }

    public static ArrayList<Potion> GetPotions(BufferedImage img){
        ArrayList<Potion> list = new ArrayList<>();
        for (int i = 0; i < img.getHeight(); i++){
            for (int j = 0; j < img.getWidth(); j++) {
                Color color = new Color(img.getRGB(j, i));
                int value = color.getBlue();
                if (value == RED_POTION || value == BLUE_POTION) {
                    list.add(new Potion(j * Game.TILES_SIZE, i * Game.TILES_SIZE, value));

                }
            }
        }
        return list;
    }

    public static ArrayList<GameContainer> GetContainers(BufferedImage img){
        ArrayList<GameContainer> list = new ArrayList<>();
        for (int i = 0; i < img.getHeight(); i++){
            for (int j = 0; j < img.getWidth(); j++){
                Color color = new Color(img.getRGB(j,i));
                int value = color.getBlue();
                if(value == BOX || value == BARREL) {
                    list.add(new GameContainer(j * Game.TILES_SIZE, i * Game.TILES_SIZE, value));
                }
            }
        }
        return list;
    }

    public static ArrayList<Spike> GetSpikes(BufferedImage img){
        ArrayList<Spike> list = new ArrayList<>();
        for (int i = 0; i < img.getHeight(); i++){
            for (int j = 0; j < img.getWidth(); j++){
                Color color = new Color(img.getRGB(j,i));
                int value = color.getBlue();
                if(value == SPIKE) {
                    list.add(new Spike(j * Game.TILES_SIZE, i * Game.TILES_SIZE, SPIKE));
                }
            }
        }
        return list;
    }

    public static ArrayList<Cannon> GetCannons(BufferedImage img){
        ArrayList<Cannon> list = new ArrayList<>();
        for (int i = 0; i < img.getHeight(); i++){
            for (int j = 0; j < img.getWidth(); j++){
                Color color = new Color(img.getRGB(j,i));
                int value = color.getBlue();
                if(value == CANNON_LEFT || value == CANNON_RIGHT) {
                    list.add(new Cannon(j * Game.TILES_SIZE, i * Game.TILES_SIZE, value));
                }
            }
        }
        return list;
    }


}
