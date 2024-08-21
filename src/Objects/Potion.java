package Objects;

import Main.Game;

/*
Class for all potions
 */
public class Potion extends GameObject{

    private float hoverOffset;
    private int maxHoverOffset, hoverDir = 1;

    public Potion(int x, int y, int objType) {
        super(x, y, objType);
        doAnimation = true; //potion will always animate as long as its active
        initHitbox(7, 14);
        xDrawOffset = (int)(3 * Game.SCALE); //whte space between picture border and potion is actually 3 pixels wide and 2 pixels tall
        yDrawOffset = (int)(2 * Game.SCALE);

        maxHoverOffset = (int)(10 * Game.SCALE);

    }

    public void update(){
        updateAnimationTick();
        updateHover();
    }

    private void updateHover() {
        hoverOffset += (0.075f * Game.SCALE * hoverDir);

        if(hoverOffset >= maxHoverOffset)
            hoverDir =-1;
        else if(hoverOffset < 0)
            hoverOffset = 1;


        hitbox.y = y + hoverOffset;
    }


}
