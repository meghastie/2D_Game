package Objects;

import Main.Game;

public class Cannon extends GameObject{

    private int tileY; //everytime we check if cannon can see player, we first make sur ethey are in same y axis
    public Cannon(int x, int y, int objType) {
        super(x, y, objType);
        tileY = y / Game.TILES_SIZE;
        initHitbox(40, 26);
        hitbox.x -= (int)(4 * Game.SCALE); //-4x and 6y will pos cannon to bottom / floor
        hitbox.y += (int)(6 * Game.SCALE);
    }

    public void update(){
        if(doAnimation){
            updateAnimationTick();;
        }
    }

    public int getTileY(){
        return tileY;
    }

}
