package Entities;

import Utilz.LoadSave;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import static Utilz.Constants.Directions.*;
import static Utilz.Constants.Directions.DOWN;
import static Utilz.Constants.PlayerConstants.*;

public class Player extends Entity{

    private BufferedImage[][] animations;
    private int aniTick, aniIndex, aniSpeed = 15; //anispeed - lower anispeed faster animaton will go
    private int playerAction = IDLE;
    private boolean left, up, right, down;
    private boolean moving = false, attacking = false;
    private float playerSpeed = 2.0f;

    public Player(float x, float y) {
        super(x, y);
        loadAnimations();
    }

    public void update() {
        updatePos();
        updateAnimationTick();
        setAnimation();
    }

    public void render(Graphics g) {
        g.drawImage(animations[playerAction][aniIndex], (int) x, (int) y, 256, 160, null); //xDelta and yDelta allows us to control sprite, have to cast to int
    }

    private void updateAnimationTick() {

        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= GetSpriteAmount(playerAction)) { //before we had this as >= 6, which would cause sprite to glitch as some actions have less than 6 sprite animations
                aniIndex = 0;
                attacking = false;
            }
        }

    }

    private void setAnimation() {

        int startAni = playerAction;

        if(moving){
            playerAction = RUNNING;
        } else {
            playerAction = IDLE;
        }

        if (attacking) {
            playerAction = ATTACK_1;
        }

        if (startAni != playerAction) {
            resetAniTick();
        }
    }

    private void resetAniTick() {
        aniTick = 0;
        aniIndex = 0;
    }

    private void updatePos() {
        moving = false; //automatically reset moving - sprite is still
        /*
        if pres left&right and up&down at same time, cancel out, so sprite should stay still. if any of the below is true, moving = true, so setAnimation above would set the animation to running
         */
        if(left && !right) { //check if pressing just left
            x-=playerSpeed;
            moving = true;
        } else if (right && !left) { //check if pressing just right
            x+=playerSpeed;
            moving = true;
        }
        if(up && !down) { //check if pressing just up
            y-=playerSpeed;
            moving = true;
        } else if (down && !up) { //check if pressing just right
            y+=playerSpeed;
            moving = true;
        }

    }

    private void loadAnimations() {
            BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);
            animations = new BufferedImage[9][6]; //y is 9 (9 down the way), x is 6 (6 along way) on sprite sheet

            for (int j = 0; j < animations.length; j++) {
                for (int i = 0; i < animations[j].length; i++) {
                    animations[j][i] = img.getSubimage(i * 64, j * 40, 64, 40); //each sprite is 64 bits long so 0*64 will get first sprite, 1 *64 will get next, etc. each is 40 long
                }
            }
    }

    public void resetDirBooleans() {
        left = false;
        right = false;
        up = false;
        down = false;
    }

    public void setAttacking(boolean attacking) {
        this.attacking = attacking;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
    }
}



