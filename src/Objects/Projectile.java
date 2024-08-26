package Objects;

/*
for cannon ball
 */

import Main.Game;

import java.awt.geom.Rectangle2D;

import static Utilz.Constants.Projectiles.*;

public class Projectile {
    private Rectangle2D.Float hitbox;
    private int dir; //cannon balls can only go left or right - speed * dir. for left dir = -1, so 0.5 * -1 = -.05. for right dir = 1, so 0.5*1 = 0.5.
    private boolean active = true;

    public Projectile(int x, int y, int dir){
        //cannon ball spawn pointa
        int xOffset = (int)(-3 * Game.SCALE);//default if facing to left
        int yOffset = (int)(5 * Game.SCALE);//will be same for both left & right

        if(dir == 1){
             xOffset = (int)(29 * Game.SCALE);
        }
        hitbox = new Rectangle2D.Float(x + xOffset + yOffset,y,CANNON_BALL_WIDTH,CANNON_BALL_HEIGHT);
        this.dir = dir;
    }

    public void updatePos(){
        hitbox.x += dir * SPEED; //if dir is 1 going to right, if dir is -1 then left
    }

    public void setPos(int x, int y){
        hitbox.x = x;
        hitbox.y = y;
    }

    public Rectangle2D.Float getHitbox(){
        return hitbox;
    }

    public boolean isActive(){
        return active;
    }

    public void setActive(Boolean active){
        this.active = active;
    }

}
