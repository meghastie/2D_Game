package Entities;

import Main.Game;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import static Utilz.Constants.Directions.LEFT;
import static Utilz.Constants.Directions.*;
import static Utilz.Constants.EnemyConstants.*;
import static Utilz.HelpMethods.*;

public class Crabby extends Enemy{

    private int attackBoxOffsetX;

    public Crabby(float x, float y) {
        super(x, y, CRABBY_WIDTH, CRABBY_HEIGHT, CRABBY);
        initHitbox(22,19); //width and height of crab is 22x19
        initAttackBox();
    }

    private void initAttackBox() {
        attackBox = new Rectangle2D.Float(x,y,(int)(82 * Game.SCALE),(int)(19 * Game.SCALE)); //30+22+30 = 82 - attackbox is a logn rectangle over crabby, as it attacks by swinging arms out. 30 is length of both arms and 22 is the bpody of crab
        attackBoxOffsetX = (int)(Game.SCALE * 30);
    }

    public void update(int[][] lvlData, Player player) {
        updateBehaviour(lvlData, player);
        updateAnimationTick();
        updateAttackBox();
    }

    private void updateAttackBox() {
        attackBox.x = hitbox.x - attackBoxOffsetX;
        attackBox.y = hitbox.y;
    }

    private void updateBehaviour(int[][] lvlData, Player player) {
        if (firstUpdate) {
            firstUpdateCheck(lvlData);
        }

        if (inAir) { //enemy will fall to floor and never be in air again
           updateInAir(lvlData);
        } else {
            switch (state) {
                case IDLE:
                    newState(RUNNING);
                    break;
                case RUNNING:

                    if(canSeePlayer(lvlData, player)){
                        turnTowardsPlayer(player);
                        if(isPlayerCloseforAttack(player)){
                            newState(ATTACK);
                        }
                    }
                    move(lvlData);
                    break;
                case ATTACK:
                    if(aniIndex == 0){ //everytime animation restarts, reset attackChecked
                        attackChecked = false;
                    }
                    if(aniIndex == 3 && !attackChecked) //only check if we are huting the player when we are at animation 3, only do one check per animation
                        checkEnemyHit(attackBox, player);
                    break;
                case HIT:
                    break;
            }
        }

    }

    public int flipX(){
        if(walkDir == RIGHT){
            return width;
        }else{
            return 0;
        }
    }

    public int flipW(){
        if(walkDir == RIGHT){
            return -1;
        }else{
            return 1;
        }
    }


}
