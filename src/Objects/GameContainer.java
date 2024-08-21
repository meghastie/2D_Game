package Objects;

import Main.Game;

import static Utilz.Constants.ObjectConstants.*;

public class GameContainer extends  GameObject{
    public GameContainer(int x, int y, int objType) {
        super(x, y, objType);
        createHitbox();
    }

    private void createHitbox() {

        if(objType == BOX){
            initHitbox(25,18);
            xDrawOffset = (int)(7* Game.SCALE);
            yDrawOffset = (int)(12 * Game.SCALE);
        } else{
            initHitbox(23,25);
            xDrawOffset = (int)(8* Game.SCALE);
            yDrawOffset = (int)(5 * Game.SCALE);
        }

        hitbox.y += yDrawOffset + (int) (Game.SCALE * 2); //ensures box and barell touches floor
        hitbox.x += xDrawOffset / 2; //sprite is 30 pixels in height and tile is 32 so thast where two comes from. diff between sprite and tike size
    }

    public void update(){
        if(doAnimation){ //dont always want to update boxes and barrels
            updateAnimationTick();
        }
    }
}
