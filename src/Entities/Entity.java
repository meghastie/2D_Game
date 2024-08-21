package Entities;

import Main.Game;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public abstract class Entity {
    protected float x, y; //protected - only subclasses can use
    protected int width, height;
    protected Rectangle2D.Float hitbox;
    protected int aniTick, aniIndex;
    protected int state;
    protected float airSpeed;
    protected boolean inAir = false;

    protected int maxHealth = 100;
    protected int currentHealth = maxHealth;

    protected Rectangle2D.Float attackBox;
    protected float walkSpeed;
    public Entity(float x, float y, int width, int height) {
        this.x = x;
        this.y =y;
        this.width = width;
        this.height = height;

    }

    //helper method to see attackbox
    protected void drawAttackBox(Graphics g, int lvlOffsetX) {
        g.setColor(Color.red);
        g.drawRect((int)attackBox.x - lvlOffsetX, (int)attackBox.y, (int)attackBox.width, (int)attackBox.height);
    }

    /*
    protected void drawHitbox(Graphics g, xLvlOffset){
        //for debugging hitbox
        g.setColor(Color.PINK);
        g.drawRect((int)hitbox.x - xLvlOffset, (int)hitbox.y, (int)hitbox.width, (int)hitbox.height);
    }
     */

    /*
    Collison 101:
        * check player wont overlap with tiles.
        * the size of the image is 64(h)x40(w), however, this is not the atual size of the player (white space around it as its an image). once we can stop this overlap, we will make an actual 'hitbox' for player (small rectangle aorund the actual player)
        * hitbox will be foundation for collision detection. The actual player will have 4 corners : top left(x,y) , top right(x+width,y), bottom left(x,y+height), bottom right(x+width,y+height)
        * if any of the above points hit a tile, player cannot move there
     */
    protected void initHitbox(int width,int height) {
        //first hitbox for entire sprite image
        hitbox = new Rectangle2D.Float(x, y, (int)(width * Game.SCALE), (int)(height * Game.SCALE));
    }

    /*
    protected void updateHitbox() {
        hitbox.x = (int) x; //will take next x and y and put it to out hitbox
        hitbox.y = (int) y;
    }
     */

    public Rectangle2D.Float getHitbox(){
        return hitbox;
    }

    public int getState() {
        return state;
    }

    public int getAniIndex() {
        return aniIndex;
    }
}
