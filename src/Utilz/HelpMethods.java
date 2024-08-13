package Utilz;

import Main.Game;

public class HelpMethods {

    /*
    check if pos overlaps any tiles
     */
    public static Boolean CanMoveHere(float x, float y, float width, float height, int[][]lvlData){ //pass in width and height of hitbox, not sprite
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
    private static Boolean IsSolid(float x, float y, int[][] lvlData){
        if (x < 0 | x >= Game.GAME_WIDTH) {
            return true; //it is solid - dont go here
        }
        if (y < 0 || y >= Game.GAME_HEIGHT){
            return true;
        }
        //if none fails, we are inside game window, so have to check where we are (where in lvlData)

        float xIndex = x / Game.TILES_SIZE;
        float yIndex = y / Game.TILES_SIZE;

        int value = lvlData[(int) yIndex][(int) xIndex]; //where we are in level

        if(value >= 48 || value < 0 || value != 11){ //check if value is a tile - 48 sprites, 48 and up will be no tiles at all. below 0 is not a tile. 11th sprite is transparent, so can pass throught(see lvlatlas) e.g. not solid
            return true;
        } else {
            return false;
        }
    }
}
