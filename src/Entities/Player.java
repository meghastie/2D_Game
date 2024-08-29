package Entities;

import Audio.AudioPlayer;
import Gamestates.Playing;
import Main.Game;
import Objects.Potion;
import Utilz.LoadSave;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import static Utilz.Constants.ANI_SPEED;
import static Utilz.Constants.Directions.*;
import static Utilz.Constants.Directions.DOWN;
import static Utilz.Constants.GRAVITY;
import static Utilz.Constants.PlayerConstants.*;
import static Utilz.HelpMethods.*;

public class Player extends Entity{

    private BufferedImage[][] animations;

    private boolean left, right, jump;
    private boolean moving = false, attacking = false;
    private int[][] lvlData;
    private float xDrawOffset = 21 * Game.SCALE; //0.0 to 21.x,4y
    private float yDrawoffset = 4 * Game.SCALE;

    //jumping/gravity
    private float jumpSpeed= -2.25f * Game.SCALE;
    private float fallSpeedAfterCollison = 0.5f * Game.SCALE;
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

    private int powerBarWidth = (int) (104 * Game.SCALE);
    private int powerBarHeight = (int) (2 * Game.SCALE);
    private int powerBarXStart = (int) (44 * Game.SCALE);
    private int powerBarYStart = (int) (34 * Game.SCALE);
    private int powerWidth = powerBarWidth;
    private int powerMaxValue = 200;
    private int powerValue = powerMaxValue;

    private int healthWidth = healthBarWidth;

    //AttackBox
    private Rectangle2D.Float attackBox; //when player pressed attack, if enemy is within this box enemy will lose health or die

    private int flipX = 0;
    private int flipW = 1;
    private Playing playing;

    private int tileY = 0;
    private boolean powerAttackActive;
    private int powerAttackTick; //will increase one every update for as long as the power attack is active. once it reaches limit, stop attack
    private int powerGrowSpeed = 15; //how much power will increase
    private int powerGrowTick;


    public Player(float x, float y, int width, int height, Playing playing) {
        super(x, y, width, height);
        this.playing = playing;
        this.state = IDLE;
        this.maxHealth = 100;
        this.currentHealth = maxHealth;
        this.walkSpeed = 1.0f * Game.SCALE;
        loadAnimations();
        initHitbox(20,27); //player is roughlt 20x28
        initAttackBox();
    }

    //if we die, reset spawn point
    public void setSpawn(Point spawn){
        this.x = spawn.x;
        this.y = spawn.y;
        hitbox.x = x;
        hitbox.y = y;
    }

    private void initAttackBox() {
        attackBox = new Rectangle2D.Float(x,y, (int) (20 * Game.SCALE), (int) (20 * Game.SCALE));
        resetAttackBox();
    }


    public void update() {
        updateHealthBar();
        updatePowerBar();
        if(currentHealth <= 0){
            if(state != DEAD){ //start of death
                state = DEAD;
                aniTick = 0;
                aniIndex = 0;
                playing.setPlayerDying(true);
                playing.getGame().getAudioPlayer().playEffect(AudioPlayer.DIE);
            }else if(aniIndex == GetSpriteAmount(DEAD) - 1 && aniTick >= ANI_SPEED -1 ){ //-1 as if there are 7 sprites, cant check 7th animation as starts at 0. first part checks last sprite, second is last animation tick. end of death
                playing.setGameOver(true);
                playing.getGame().getAudioPlayer().stopSong();
                playing.getGame().getAudioPlayer().playEffect(AudioPlayer.GAMEOVER);
            }else{
                updateAnimationTick(); //if not at start / end pf death sequenece, continue updating
            }

            //playing.setGameOver(true);
            return; //return as as soon as we are dead, dont want to update attackbox, position etc
        }

        updateAttackBox();

        updatePos();
        if(moving){
            checkPotionTouched();
            checkSpikesTouched();
            tileY = (int) (hitbox.y / Game.TILES_SIZE);
            if(powerAttackActive){ //will always be movig when power attack
                powerAttackTick++;
                if(powerAttackTick >= 35){
                    powerAttackTick = 0;
                    powerAttackActive = false;
                }
            }
        }
        if(attacking || powerAttackActive){
            checkAttack();
        }
        updateAnimationTick();
        setAnimation();
    }



    private void checkSpikesTouched() {
        playing.checkSpikesTouched(this);
    }

    private void checkPotionTouched() {
        playing.checkPotionTouched(hitbox);
    }

    private void  checkAttack() { //check we are on correct sprite index and we haven checked before
        if(attackChecked || aniIndex != 1){
            return;
        }
        attackChecked = true;

        if(powerAttackActive){
            attackChecked = false;  //makes sure eery update we are checking attack again
        }

        playing.checkEnemyHit(attackBox);
        playing.checkObjectHit(attackBox);
        playing.getGame().getAudioPlayer().playAttackSound();

    }

    private void updateAttackBox() {
        if(right || left){
            if(flipW == 1){
                attackBox.x = hitbox.x + hitbox.width + (int)(Game.SCALE * 10);
            }else{
                attackBox.x = hitbox.x - hitbox.width - (int)(Game.SCALE * 10);
            }
        }
        else if(right || (powerAttackActive && flipW == 1)){
            attackBox.x = hitbox.x + hitbox.width + (int)(Game.SCALE * 10);
        } else if(left || (powerAttackActive && flipW == -1)){
            attackBox.x = hitbox.x - hitbox.width - (int)(Game.SCALE * 10);
        }
        attackBox.y = hitbox.y + (Game.SCALE * 10);
    }

    private void updateHealthBar() {
        healthWidth = (int)((currentHealth / (float) maxHealth) * healthBarWidth);
    }

    private void updatePowerBar(){
        powerWidth = (int) ((powerValue / (float) powerMaxValue) * powerBarWidth);

        powerGrowTick++; //power will build slowly as time passes
        if (powerGrowTick >= powerGrowSpeed) {
            powerGrowTick = 0;
            changePower(1);
        }
    }



    public void render(Graphics g, int lvlOffset) {
        g.drawImage(animations[state][aniIndex], (int)(hitbox.x - xDrawOffset) - lvlOffset + flipX, (int)(hitbox.y - yDrawoffset), width * flipW, height, null); //x and y of hitbox, width and height of sprite
        //drawHitbox(g, lvlOffset);
        //drawAttackBox(g, lvlOffset);
        drawUI(g);
    }

    private void drawUI(Graphics g) {
        //background for health and power
        g.drawImage(statusBarImg, statusBarX, statusBarY, statusBarWidth, statusBarHeight, null);
        //healthbar
        g.setColor(Color.red);
        g.fillRect(healthBarXStart + statusBarX, healthBarYStart + statusBarY, healthWidth, healthBarHeight); //status bar x/y is offset between top of whole image and width where ir starts after the heart icon
        //powerbar
        g.setColor(Color.yellow);
        g.fillRect(powerBarXStart + statusBarX, powerBarYStart + statusBarY, powerWidth, powerBarHeight);
    }

    private void updateAnimationTick() {

        aniTick++;
        if (aniTick >= ANI_SPEED) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= GetSpriteAmount(state)) { //before we had this as >= 6, which would cause sprite to glitch as some actions have less than 6 sprite animations
                aniIndex = 0;
                attacking = false;
                attackChecked = false;
            }
        }

    }

    private void setAnimation() {

        int startAni = state;

        if(moving){
            state = RUNNING;
        } else {
            state = IDLE;
        }

        if (inAir){
            if (airSpeed < 0){
                state = JUMP;
            } else {
                state = FALLING;
            }

        }

        if(powerAttackActive){
            state = ATTACK;
            aniIndex = 1;
            aniTick = 0;
            return;
        }

        if (attacking) {
            state = ATTACK;
            if(startAni != ATTACK){ //wasnt attacking when entering this method
                aniIndex = 1; //set index to one if not attacked before to attack instantly
                aniTick = 0;
                return;
            }
        }

        if (startAni != state) {
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
            if(!powerAttackActive){
                if((!left && !right) || (right && left)){ //if not holding down any button or in air no point in being here - standng still
                    return;
                }
            }
        }
        float xSpeed = 0; //temp storage of speed in x dir
        /*
        if pres left&right and up&down at same time, cancel out, so sprite should stay still. if any of the below is true, moving = true, so setAnimation above would set the animation to running
         */


        if(left && !right) { //check if pressing just left
            xSpeed -= walkSpeed;
            flipX = width;
            flipW = -1;
        }

        if (right && !left) { //check if pressing just right
            xSpeed += walkSpeed;
            flipX = 0;
            flipW = 1;
        }

        if(powerAttackActive){ //this will make the sprite move faster wehn power attacking
            if((!left && !right)||(left && right)){
                    if(flipW == -1){ //facing left
                        xSpeed = -walkSpeed;
                    }else{
                        xSpeed = walkSpeed;
                    }
            }
            xSpeed *= 3; //3 times speed if in a power attack
        }

        if (!inAir){
            if (!IsEntityOnFloor(hitbox, lvlData)){
                inAir = true;
            }
        }

        if (inAir && !powerAttackActive){ //if in air, only need to check in y direction for collisions. dont want to update y if in power attack. if jump and press ower attack, just go straight but faster. when power attack ends, start updating y again
            if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)){
                hitbox.y += airSpeed; //airSpeed will incrase over time
                airSpeed += GRAVITY;
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
        playing.getGame().getAudioPlayer().playEffect(AudioPlayer.JUMP);
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
            if(powerAttackActive){ //if we hit a wall, stop
                powerAttackActive = false;
                powerAttackTick = 0;
            }
        }
    }

    public void changePower(int value){
        powerValue += value;
        if (powerValue >= powerMaxValue)
            powerValue = powerMaxValue;
        else if (powerValue <= 0)
            powerValue = 0;
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

    public void kill(){
        currentHealth = 0;
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
    }

    public void setAttacking(boolean attacking) {
        this.attacking = attacking;
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


    public void setJump(boolean jump) {
        this.jump = jump;
    }

    public void resetAll(){
        resetDirBooleans();
        inAir = false;
        attacking = false;
        moving = false;
        airSpeed = 0f;
        state = IDLE;
        currentHealth = maxHealth;

        hitbox.x = x; //go back to start point
        hitbox.y = y;

        resetAttackBox();

        if (!IsEntityOnFloor(hitbox, lvlData)){
            inAir = true;
        }

    }

    private void resetAttackBox(){
        if(flipW == 1){
            attackBox.x = hitbox.x + hitbox.width + (int)(Game.SCALE * 10);
        }else{
            attackBox.x = hitbox.x - hitbox.width - (int)(Game.SCALE * 10);
        }
    }

    public int getTileY(){
        return tileY;
    }

    public void powerAttack(){
        if(powerAttackActive){
            return;
        }
        if(powerValue >= 60){
            powerAttackActive = true;
            changePower(-60);
        }
    }
}



