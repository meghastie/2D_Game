package Entities;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import Gamestates.Playing;
import Levels.Level;
import Utilz.LoadSave;
import static Utilz.Constants.EnemyConstants.*;

/*
All code necessary for enemies to work
 */

public class EnemyManager {

    private Playing playing;
    private BufferedImage[][] crabbyArr;
    private ArrayList<Crabby> crabbies = new ArrayList<>();

    public EnemyManager(Playing playing) {
        this.playing = playing;
        loadEnemyImages();
    }

    public void loadEnemies(Level level) {
        crabbies = level.getCrabs();
        //System.out.println("size of crabs: " + crabbies.size());
    }

    public void update(int[][] lvlData, Player player) {
        boolean isAnyActive = false;
        for (Crabby c : crabbies){
            if(c.isActive()){
                c.update(lvlData, player);
                isAnyActive = true;
            }
        }
        if(!isAnyActive){
            playing.setLvlCompleted(true);
        }

    }

    public void draw(Graphics g, int xLvlOffset) {
        drawCrabs(g, xLvlOffset);
    }

    private void drawCrabs(Graphics g, int xLvlOffset) {
        for (Crabby c : crabbies) {
            if(c.isActive()){
                g.drawImage(crabbyArr[c.getEnemyState()][c.getAniIndex()],
                        (int) c.getHitbox().x - xLvlOffset - CRABBY_DRAWOFFSET_X + c.flipX(),
                        (int) c.getHitbox().y - CRABBY_DRAWOFFSET_Y,
                        CRABBY_WIDTH * c.flipW(),
                        CRABBY_HEIGHT,
                        null);
            /*
            crabbyArray[c.getEnemyState()][c.getAniIndex()]: This represents the current frame of the crabby entity's animation based on its state and animation index.
            (int) (c.getHitbox().x - CRABBY_DRAWOFFSET_X): The X-coordinate where the image will be drawn. The c.getHitbox().x gives the X-coordinate of the entity's hitbox, and CRABBY_DRAWOFFSET_X is subtracted as an offset. - xLvlOffset: Further adjusts the X-coordinate based on the level's offset, which is likely used to manage scrolling or camera movement.
            (int) (c.getHitbox().y - CRABBY_DRAWOFFSET_Y): Similar to the X-coordinate, but for the Y-coordinate.
            CRABBY_WIDTH and CRABBY_HEIGHT: These define the width and height of the image to be drawn.
             */
                //c.drawAttackBox(g, xLvlOffset);
            }
        }
    }

    public void checkEnemyHit(Rectangle2D.Float attackBox){
        for(Crabby c: crabbies){
            if(c.isActive()){
                if(attackBox.intersects(c.getHitbox())){ //if returns true, player did hit an enemy
                    c.hurt(10);
                    return; //player should only be able to hit one enemy at a time
                }
            }
        }
    }

    private void loadEnemyImages() {  //5 diff enemy states max 9 indexes

        crabbyArr = new BufferedImage[5][9];
        BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.CRABBY_SPRITE);
        for (int j = 0; j < crabbyArr.length; j++)
            for (int i = 0; i < crabbyArr[j].length; i++)
                crabbyArr[j][i] = temp.getSubimage(i * CRABBY_WIDTH_DEFAULT, j * CRABBY_HEIGHT_DEFAULT, CRABBY_WIDTH_DEFAULT, CRABBY_HEIGHT_DEFAULT);
    }

    public void resetAllEnemies(){
        for(Crabby c: crabbies){
            c.resetEnemy();
        }
    }

}