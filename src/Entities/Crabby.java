package Entities;

import Main.Game;

import static Utilz.Constants.Directions.LEFT;
import static Utilz.Constants.EnemyConstants.*;
import static Utilz.HelpMethods.*;

public class Crabby extends Enemy{

    public Crabby(float x, float y) {
        super(x, y, CRABBY_WIDTH, CRABBY_HEIGHT, CRABBY);
        initHitbox(x,y,(int)(22 * Game.SCALE),(int)(19 * Game.SCALE)); //width and height of crab is 22x19
    }

    public void update(int[][] lvlData, Player player) {
        updateMove(lvlData, player);
        updateAnimationTick();

    }

    private void updateMove(int[][] lvlData, Player player) {
        if (firstUpdate) {
            firstUpdateCheck(lvlData);
        }

        if (inAir) { //enemy will fall to floor and never be in air again
           updateInAir(lvlData);
        } else {
            switch (enemyState) {
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
            }
        }

    }


}
