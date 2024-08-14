package Levels;

public class Level {

    /*
    lvlData is a 2D array of integers (int[][]). Each element in this array corresponds to a specific tile or block in the game world. The first index represents the row (y-axis), and the second index represents the column (x-axis). This forms a grid, with each cell in the grid representing a piece of the level.
     Each value in the lvlData array represents an index that corresponds to a specific sprite or tile in a sprite sheet. For example, if lvlData[y][x] = 3, it could mean that the tile at position (x, y) in the level should be drawn using the sprite located at index 3 in the sprite sheet.
     */
    private int[][] lvlData;

    public Level(int[][] lvlData) {
        this.lvlData = lvlData;
    }

    public int getSpriteIndex(int x, int y){
        return lvlData[y][x]; //y-axis, x-axis
        //the value returned is the index of the sprite that should be rendered at that specific position in the level. By using this method, the game can determine which sprite to draw based on the current position within the level grid
    }

    public int[][] getLvlData(){
        return lvlData;
    }

}
