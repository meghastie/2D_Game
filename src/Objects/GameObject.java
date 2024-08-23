package Objects;

import Main.Game;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import static Utilz.Constants.ANI_SPEED;
import static Utilz.Constants.ObjectConstants.*;


public class GameObject {
    
    protected int x,y, objType;
    protected Rectangle2D.Float hitbox;
    protected boolean doAnimation, active = true; //pick it up or destory it - animation wont be active anymoew
    protected int aniTick, aniIndex;
    protected int xDrawOffset;
    protected int yDrawOffset;

    public GameObject(int x, int y, int objType){
        this.x = x;
        this. y = y;
        this.objType = objType;
    }

    protected void updateAnimationTick() {
        aniTick++;
        if (aniTick >= ANI_SPEED) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= GetSpriteAmount(objType)) {
                aniIndex = 0;
                if(objType == BARREL || objType == BOX){ //box and barrel only animated when being destroyed
                    doAnimation = false;
                    active = false;
                }else if(objType == CANNON_LEFT || objType == CANNON_RIGHT){ //cannon shoots once, checks if player is visible then shoots again. one animation per shot
                    doAnimation = false;
                }
            }
        }
    }

    public void reset(){
        aniIndex = 0;
        aniTick = 0;
        active = true;

        if(objType == BARREL || objType == BOX || objType == CANNON_RIGHT || objType == CANNON_LEFT){ //if barrell or box or cannon dont want to animate from start
            doAnimation = false;
        } else{
            doAnimation = true; //if potion want to animate from start
        }
    }

    protected void initHitbox(int width,int height) {
        hitbox = new Rectangle2D.Float(x, y, (int)(width * Game.SCALE), (int)(height * Game.SCALE));
    }

    public void drawHitbox(Graphics g, int xLvlOffset){
        g.setColor(Color.PINK);
        g.drawRect((int)hitbox.x - xLvlOffset, (int)hitbox.y, (int)hitbox.width, (int)hitbox.height);
    }

    public int getObjType() {
        return objType;
    }

    public void setObjType(int objType) {
        this.objType = objType;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active){
        this.active = active;
    }

    public Rectangle2D.Float getHitbox() {
        return hitbox;
    }

    public void setAnimation(Boolean doAnimation){
        this.doAnimation = doAnimation;
    }


    public int getxDrawOffset() {
        return xDrawOffset;
    }


    public int getyDrawOffset() {
        return yDrawOffset;
    }

    public int getAniIndex(){
        return aniIndex;
    }

    public int getAniTick(){
        return aniTick;
    }
}
