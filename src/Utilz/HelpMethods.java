package Utilz;

import Main.Game;

import java.awt.geom.Rectangle2D;

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

    public static boolean IsAllTilesWalkable(int xStart, int xEnd, int y, int[][] lvlData) {
        for (int i = 0; i < xEnd - xStart; i++) { //i < diff between tiles
            if (IsTileSolid(xStart + i, y, lvlData)) { // start with second as second tile is smaller than first
                return false;
            }
            if (!IsTileSolid(xStart + i, y + 1, lvlData)) {
                return false;
            }
        }
        return true;
    }

    public static boolean IsFloor(Rectangle2D.Float hitbox, float xSpeed, int[][] lvlData) {
        return IsSolid(hitbox.x + xSpeed, hitbox.y + hitbox.height + 1, lvlData); //if its solid we can walk ther. hitbox.y + hitbox.height + 1 - not checking hitbox, checking pixel below
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

}
