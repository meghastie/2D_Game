package Entities;

import static Utilz.Constants.ANI_SPEED;
import static Utilz.Constants.EnemyConstants.*;
import static Utilz.Constants.GRAVITY;
import static Utilz.HelpMethods.*;
import static Utilz.Constants.Directions.*;

import Main.Game;

import java.awt.geom.Rectangle2D;

/*
super class for all enemies
 */

public abstract class Enemy extends Entity {
    protected int enemyType;
    protected boolean firstUpdate = true;
    protected int walkDir = LEFT;
    protected int tileY;
    protected float attackDistance = Game.TILES_SIZE; //attack distance is size of a tile
    protected boolean active = true; //enemy s active when we start game
    protected boolean attackChecked;

    public Enemy(float x, float y, int width, int height, int enemyType) {
        super(x, y, width, height);
        this.enemyType = enemyType;
        maxHealth = GetMaxHealth(enemyType);
        currentHealth = maxHealth;
        walkSpeed = Game.SCALE * 0.35f;

    }

    protected void firstUpdateCheck(int[][] lvlData){
            if (!IsEntityOnFloor(hitbox, lvlData)){
                inAir = true;
            }
            firstUpdate = false;
    }

    protected void updateInAir(int[][] lvlData){
            if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)) {
                hitbox.y += airSpeed;
                airSpeed += GRAVITY;
            } else {
                inAir = false;
                hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed);
                tileY = (int) (hitbox.y / Game.TILES_SIZE); //GET TILE Y FOR ENEMY WHICH WILL NEVER CHANGE
            }
    }

    protected void move(int[][] lvlData){
        float xSpeed = 0;

        if (walkDir == LEFT) {
            xSpeed = -walkSpeed;
        }else {
            xSpeed = walkSpeed;
        }

        if (CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData)) {
            if (IsFloor(hitbox, xSpeed, lvlData)) {
                hitbox.x += xSpeed;
                return; //if both above true, we have the return here so never enter the else below (change dir)
            }
        }
            changeWalkDir(); //will only get here if either of the two above if statemenrs return false
    }

    protected void turnTowardsPlayer(Player player){
        if(player.hitbox.x > hitbox.x){
            walkDir = RIGHT;
        } else{
            walkDir = LEFT;
        }
    }

    protected boolean canSeePlayer(int[][] lvlData, Player player){
        //check they are on same tile in y axis
        int playerTileY = (int) (player.getHitbox().y / Game.TILES_SIZE); //GET CURRENT Y POS OF PLAYER AND TURN IT INTO CURRENT TILE Y
        if(playerTileY == tileY){
            if(isPlayerInRange(player)){
                if(IsSightClear(lvlData, hitbox, player.hitbox, tileY)){ //make sure there are no obstav=cles - pits, tiles
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean isPlayerInRange(Player player) { //check if player is in line of sight
        int absValue = (int) Math.abs(player.hitbox.x - hitbox.x); //abs is distance between two points. will never return negative
        return absValue <= attackDistance * 5; //if distance between player and enemy hitbox is less than 5 tiles, return true in first if sttamenet in canSeePlayer
    }

    protected boolean isPlayerCloseforAttack(Player player){ //if player is close enough to attack (above is 5 tiles, this is one tile)
        int absValue = (int) Math.abs(player.hitbox.x - hitbox.x);
        return absValue <= attackDistance;
    }

    protected void newState(int enemyState){
        this.state = enemyState;
        aniTick = 0;
        aniIndex = 0;
    }

    public void hurt(int amount){
        currentHealth -= amount;
        if(currentHealth <= 0){
            newState(DEAD);
        }else{
            newState(HIT);
        }
    }

    protected void checkEnemyHit(Rectangle2D.Float attackBox, Player player){
        if(attackBox.intersects(player.hitbox)){
            player.changeHealth(-GetEnemyDmg(enemyType));
        }
        attackChecked = true;
    }

    protected void updateAnimationTick() {
        aniTick++;
        if (aniTick >= ANI_SPEED) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= GetSpriteAmount(enemyType, state)) {
                aniIndex = 0;

                switch (state){
                    case ATTACK,HIT -> state = IDLE;
                    case DEAD -> active = false; //if enemy dies, its no longer active,we dont want to update them etc
                }
            }
        }
    }

    protected void changeWalkDir() {
        if (walkDir == LEFT)
            walkDir = RIGHT;
        else
            walkDir = LEFT;

    }

    public void resetEnemy(){
        hitbox.x = x;
        hitbox.y = y;
        firstUpdate = true;
        currentHealth = maxHealth;
        newState(IDLE);
        active = true;
        airSpeed = 0;
    }

    public boolean isActive() {
        return active;
    }
}