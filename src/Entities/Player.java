package Entities;

import Gamestates.Playing;
import Main.Game;
import Utilz.LoadSave;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import static Utilz.Constants.Directions.*;
import static Utilz.Constants.Directions.DOWN;
import static Utilz.Constants.PlayerConstants.*;
import static Utilz.HelpMethods.*;

public class Player extends Entity{

    private BufferedImage[][] animations;
    private int aniTick, aniIndex, aniSpeed = 15; //anispeed - lower anispeed faster animaton will go
    private int playerAction = IDLE;
    private boolean left, up, right, down, jump;
    private boolean moving = false, attacking = false;
    private float playerSpeed = 1.0f * Game.SCALE;
    private int[][] lvlData;
    private float xDrawOffset = 21 * Game.SCALE; //0.0 to 21.x,4y
    private float yDrawoffset = 4 * Game.SCALE;

    //jumping/gravity
    private float airSpeed = 0f;
    private float gravity = 0.04f * Game.SCALE; //lower val, higher player can jump
    private float jumpSpeed= -2.25f * Game.SCALE;
    private float fallSpeedAfterCollison = 0.5f * Game.SCALE;
    private boolean inAir = false;
    private boolean attackChecked = false;

    //StatusBar UI
    private BufferedImage statusBarImg;

    private int statusBarWidth = (int) (192 * Game.SCALE);
    private int statusBarHeight = (int) (58 * Game.SCALE);
    private int statusBarX = (int) (10 * Game.SCALE);
    private int statusBarY = (int) (10 * Game.SCALE);

    private int healthBarWidth = (int) (150 * Game.SCALE);
    private int healthBarHeight = (int) (4 * Game.SCALE);
    private int healthBarXStart = (int) (34 * Game.SCALE);
    private int healthBarYStart = (int) (14 * Game.SCALE);

    private int maxHealth = 100;
    private int currentHealth = maxHealth;
    private int healthWidth = healthBarWidth;

    //AttackBox
    private Rectangle2D.Float attackBox; //when player pressed attack, if enemy is within this box enemy will lose health or die

    private int flipX = 0;
    private int flipW = 1;
    private Playing playing;


    public Player(float x, float y, int width, int height, Playing playing) {
        super(x, y, width, height);
        this.playing = playing;
        loadAnimations();
        initHitbox(x,y,(int) (20*Game.SCALE),(int) (27*Game.SCALE)); //player is roughlt 20x28
        initAttackBox();
    }

    private void initAttackBox() {
        attackBox = new Rectangle2D.Float(x,y, (int) (20 * Game.SCALE), (int) (20 * Game.SCALE));
    }


    public void update() {
        updateHealthBar();
        if(currentHealth <= 0){
            playing.setGameOver(true);
            return;
        }

        updateAttackBox();

        updatePos();
        if(attacking){
            checkAttack();
        }
        updateAnimationTick();
        setAnimation();
    }

    private void checkAttack() { //check we are on correct sprite index and we haven checked before
        if(attackChecked || aniIndex != 1){
            return;
        }
        attackChecked = true;
        playing.checkEnemyHit(attackBox);

    }

    private void updateAttackBox() {
        if(right){
            attackBox.x = hitbox.x + hitbox.width + (int)(Game.SCALE * 10);
        } else if(left){
            attackBox.x = hitbox.x - hitbox.width - (int)(Game.SCALE * 10);
        }
        attackBox.y = hitbox.y + (Game.SCALE * 10);
    }

    private void updateHealthBar() {
        healthWidth = (int)((currentHealth / (float) maxHealth) * healthBarWidth);
    }

    public void render(Graphics g, int lvlOffset) {
        g.drawImage(animations[playerAction][aniIndex], (int)(hitbox.x - xDrawOffset) - lvlOffset + flipX, (int)(hitbox.y - yDrawoffset), width * flipW, height, null); //x and y of hitbox, width and height of sprite
        //drawHitbox(g, lvlOffset);
        //drawAttackBox(g, lvlOffset);
        drawUI(g);
    }

    private void drawAttackBox(Graphics g, int lvlOffsetX) {
        g.setColor(Color.red);
        g.drawRect((int)attackBox.x - lvlOffsetX, (int)attackBox.y, (int)attackBox.width, (int)attackBox.height);
    }

    private void drawUI(Graphics g) {
        g.drawImage(statusBarImg, statusBarX, statusBarY, statusBarWidth, statusBarHeight, null);
        g.setColor(Color.red);
        g.fillRect(healthBarXStart + statusBarX, healthBarYStart + statusBarY, healthWidth, healthBarHeight); //status bar x/y is offset between top of whole image and width where ir starts after the heart icon
    }

    private void updateAnimationTick() {

        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= GetSpriteAmount(playerAction)) { //before we had this as >= 6, which would cause sprite to glitch as some actions have less than 6 sprite animations
                aniIndex = 0;
                attacking = false;
                attackChecked = false;
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

        if (inAir){
            if (airSpeed < 0){
                playerAction = JUMP;
            } else {
                playerAction = FALLING;
            }

        }

        if (attacking) {
            playerAction = ATTACK;
            if(startAni != ATTACK){ //wasnt attacking when entering this method
                aniIndex = 1; //set index to one if not attacked before to attack instantly
                aniTick = 0;
                return;
            }
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

        if (jump){
            jump();
        }

        if(!inAir){
            if((!left && !right) || (right && left)){ //if not holding down any button or in air no point in being here - standng still
                return;
            }
        }
        float xSpeed = 0; //temp storage of speed in x dir
        /*
        if pres left&right and up&down at same time, cancel out, so sprite should stay still. if any of the below is true, moving = true, so setAnimation above would set the animation to running
         */


        if(left) { //check if pressing just left
            xSpeed -= playerSpeed;
            flipX = width;
            flipW = -1;
        }

        if (right) { //check if pressing just right
            xSpeed += playerSpeed;
            flipX = 0;
            flipW = 1;
        }

        if (!inAir){
            if (!IsEntityOnFloor(hitbox, lvlData)){
                inAir = true;
            }
        }

        if (inAir){ //if in air, only need to check in y direction for collisions
            if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)){
                hitbox.y += airSpeed; //airSpeed will incrase over time
                airSpeed += gravity;
                updateXPos(xSpeed);
            }else { //if cant move up or down - hitting the roof or hitting the floor
                hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox,airSpeed);
                if (airSpeed > 0){
                    resetInAir();
                } else { 
                    airSpeed = fallSpeedAfterCollison;
                }
                updateXPos(xSpeed);
            }
        }else { //if nt in air only need to check in x dir for collisions
            updateXPos(xSpeed);
        }
        moving = true; //if not moving, will exit at the return sttaement
    }


    private void jump() {
        if (inAir){
            return;
        }
        inAir = true;
        airSpeed = jumpSpeed;

    }

    private void resetInAir() {
        inAir = false;
        airSpeed = 0;
    }

    private void updateXPos(float xSpeed) {
        if (CanMoveHere(hitbox.x+xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData)){ //next x pos next y pos, width height & lvlData - checking if we can move left or right
            hitbox.x += xSpeed;
        }else {
            hitbox.x = GetEntityXPosNextToWall(hitbox, xSpeed); //if we cant, then we move next o it
        }
    }

    public void changeHealth(int value){
        currentHealth+=value;

        if(currentHealth<=0){
            currentHealth = 0;
            //gameover();
        }else if(currentHealth >= maxHealth){
            currentHealth = maxHealth;
        }
    }

    private void loadAnimations() {
            BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);
            animations = new BufferedImage[7][8]; //y is 9 (9 down the way), x is 6 (6 along way) on sprite sheet

            for (int j = 0; j < animations.length; j++) {
                for (int i = 0; i < animations[j].length; i++) {
                    animations[j][i] = img.getSubimage(i * 64, j * 40, 64, 40); //each sprite is 64 bits long so 0*64 will get first sprite, 1 *64 will get next, etc. each is 40 long
                }
            }

            statusBarImg = LoadSave.GetSpriteAtlas(LoadSave.STATUS_BAR);
    }

    public void loadLvlData(int[][] lvlData) {
        this.lvlData = lvlData;
        if (!IsEntityOnFloor(hitbox, lvlData)){
            inAir = true;
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

    public void setJump(boolean jump) {
        this.jump = jump;
    }

    public void resetAll(){
        resetDirBooleans();
        inAir = false;
        attacking = false;
        moving = false;
        playerAction = IDLE;
        currentHealth = maxHealth;

        hitbox.x = x; //go back to start point
        hitbox.y = y;

        if (!IsEntityOnFloor(hitbox, lvlData)){
            inAir = true;
        }

    }
}



