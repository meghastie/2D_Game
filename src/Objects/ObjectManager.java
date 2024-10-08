package Objects;

import Entities.Player;
import Gamestates.Playing;
import Levels.Level;
import Main.Game;
import Utilz.LoadSave;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static Utilz.Constants.ObjectConstants.*;
import static Utilz.Constants.Projectiles.*;
import static Utilz.HelpMethods.CanCannonSeePlayer;
import static Utilz.HelpMethods.IsProjectileHittingLevel;

public class ObjectManager {

    private Playing playing;
    private BufferedImage[][] potionImgs, containerImgs;
    private BufferedImage[] cannonImgs;
    private BufferedImage spikeImage, cannonBallImg;
    private ArrayList<Potion> potions;
    private ArrayList<GameContainer> containers;
    private ArrayList<Spike> spikes;
    private ArrayList<Cannon> cannons;
    private ArrayList<Projectile> projectiles = new ArrayList<>();
    public ObjectManager(Playing playing){
        this.playing = playing;
        loadImgs();

        /*
        potions = new ArrayList<>();
        potions.add(new Potion(300,300,RED_POTION));
        potions.add(new Potion(400,300,BLUE_POTION));

        containers = new ArrayList<>();
        containers.add(new GameContainer(500,300,BARREL));
        containers.add(new GameContainer(600,300,BOX));
         */

    }

    public void checkSpikesTouched(Player p){
        for(Spike s : spikes){
            if(s.getHitbox().intersects(p.getHitbox())){
                p.kill();
            }
        }
    }

    //check player has touched a potion
    public void checkObjectTouched(Rectangle2D.Float hitbox){
        for(Potion p : potions){
            if(p.isActive()){
                if(hitbox.intersects(p.getHitbox())){
                    p.setActive(false);
                    applyEffectToPlayer(p);
                }
            }
        }
    }

    //what effect it iwll have on player - eitehr red or blue?
    public void applyEffectToPlayer(Potion p){
        if(p.getObjType() == RED_POTION){
            playing.getPlayer().changeHealth(RED_POTION_VALUE);
        }else{
            playing.getPlayer().changePower(BLUE_POTION_VALUE);
        }
    }

    //check if object has been hit e.g. barell destroyed
    public void checkObjectHit(Rectangle2D.Float attackbox){
        for(GameContainer gc : containers){
            if(gc.isActive() && !gc.doAnimation){
                if(gc.getHitbox().intersects(attackbox)){
                    gc.setAnimation(true);

                    int type = 0;
                    if(gc.getObjType() == BARREL){
                        type = 1;
                    }
                    potions.add(new Potion((int)(gc.getHitbox().x + gc.getHitbox().width / 2),
                            (int)(gc.getHitbox().y - gc.getHitbox().height / 2),
                            type));
                    return;
                }
            }
        }
    }


    /*
    level determines how many potions, containers, spikes, cannons etc but does not specifcally determine projectiles - every time cannon shoots we add to projectile array and update it.
     */
    public void loadObjects(Level newLevel){
        potions = new ArrayList<>(newLevel.getPotions());
        containers = new ArrayList<>(newLevel.getContainers());
        spikes = newLevel.getSpikes(); //dont need to make copy like above as will never reset spikes ands there will never be any spawning in the game
        cannons = newLevel.getCannons();
        projectiles.clear(); //want to clear whenever we start a new level. clear is a method from arrayList which removes all entries from list
    }

    private void loadImgs() {
        //get potion images
        BufferedImage potionSprite = LoadSave.GetSpriteAtlas(LoadSave.POTION_ATLAS);
        potionImgs = new BufferedImage[2][7];

        for(int i = 0; i < potionImgs.length; i++){
            for(int j = 0; j < potionImgs[i].length; j++){
                potionImgs[i][j] = potionSprite.getSubimage(12*j, 16*i, 12, 16);
            }
        }

        //get container images
        BufferedImage containerSprite = LoadSave.GetSpriteAtlas(LoadSave.CONTAINER_ATLAS);
        containerImgs = new BufferedImage[2][8];

        for(int i = 0; i < containerImgs.length; i++){
            for(int j = 0; j < containerImgs[i].length; j++){
                containerImgs[i][j] = containerSprite.getSubimage(40*j, 30*i, 40, 30);
            }
        }

        //get spike image
        spikeImage = LoadSave.GetSpriteAtlas(LoadSave.TRAP_ATLAS);

        //get cannon images
        cannonImgs = new BufferedImage[7];
        BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.CANNON_ATLAS);

        for(int i = 0; i < cannonImgs.length; i++){
            cannonImgs[i] = temp.getSubimage(i*40,0,40,26);
        }

        //cannon ball image
        cannonBallImg = LoadSave.GetSpriteAtlas(LoadSave.CANNON_BALL);
    }

    public void update(int[][] lvlData, Player player){
        for(Potion p : potions){
            if(p.isActive()){
                p.update();
            }
        }

        for(GameContainer gc : containers){
            if(gc.isActive()){
                gc.update();
            }
        }

        updateCannons(lvlData, player);
        updateProjectiles(lvlData, player);
    }

    private void updateProjectiles(int[][] lvlData, Player player) {
        for(Projectile p : projectiles){
            if(p.isActive()){
                p.updatePos();
                if(p.getHitbox().intersects(player.getHitbox())){
                    player.changeHealth(-25);
                    p.setActive(false);
                }else if(IsProjectileHittingLevel(p,lvlData)){
                    p.setActive(false);
                }
            }
        }
    }

    private void updateCannons(int[][] lvlData, Player player) {
        for(Cannon c : cannons){
            if(!c.doAnimation){
                if(c.getTileY() == player.getTileY()){
                    if(isPlayerInRange(c,player)){
                        if(isPlayerInFrontOfCannon(c,player)){
                            if(CanCannonSeePlayer(lvlData, player.getHitbox(),c.getHitbox(), c.getTileY()))
                                c.setAnimation(true); //cannon will now load up before shooting cannon ball - isnt instant
                        }
                    }
                }
            }
            c.update(); //cannons will always be active so just update is good enough
            if(c.getAniIndex() == 4 && c.getAniTick() == 0){
                shootCannon(c);
            }
        }
        /* check below if cannon is NOT animating. if animating means its abt to shoot.
         * tileY is same
         * check if player is in range
         * is player in front of cannon
         * check line of sight
         * if all true, fire cannon
         */
    }

    private void shootCannon(Cannon c) {
        int dir = 1;
        if(c.getObjType() == CANNON_LEFT){
            dir = -1;
        }
        projectiles.add(new Projectile((int)(c.getHitbox().x) , (int)(c.getHitbox().y) , dir));
    }

    private boolean isPlayerInFrontOfCannon(Cannon c, Player player) {
        if(c.getObjType() == CANNON_LEFT){
            if(c.getHitbox().x > player.getHitbox().x){ //means player is on left size of hitbox
                return true;
            }
        } else if (c.getHitbox().x < player.getHitbox().x) {
            return true;
        }
        return false;
    }

    private boolean isPlayerInRange(Cannon c, Player player) {
        int absValue = (int) Math.abs(player.getHitbox().x - c.getHitbox().x); //abs is distance between two points. will never return negative
        return absValue <= Game.TILES_SIZE * 5; //if distance between player and enemy hitbox is less than 5 tiles, return true in first if sttamenet in canSeePlayer
    }

    public void draw(Graphics g, int xLvlOffset){
        drawPotions(g,xLvlOffset);
        drawContainers(g,xLvlOffset);
        drawTraps(g, xLvlOffset);
        drawCannons(g, xLvlOffset);
        drawProjectiles(g, xLvlOffset);
    }

    private void drawProjectiles(Graphics g, int xLvlOffset) {
        for(Projectile p : projectiles){
            if(p.isActive()){
                g.drawImage(cannonBallImg, (int) (p.getHitbox().x - xLvlOffset), (int)(p.getHitbox().y), CANNON_BALL_WIDTH, CANNON_BALL_HEIGHT, null);
            }
        }
    }

    private void drawCannons(Graphics g, int xLvlOffset) {
        for (Cannon c : cannons) {
            int x = (int) (c.getHitbox().x - xLvlOffset);
            int width = CANNON_WIDTH;

            if (c.getObjType() == CANNON_RIGHT) {
                x += width; //increase x with width
                width *= -1; //facing to right so want to flip width
            }

            g.drawImage(cannonImgs[c.getAniIndex()], x, (int)(c.getHitbox().y), width, CANNON_HEIGHT, null);
        }
    }

    private void drawTraps(Graphics g, int xLvlOffset) {
        for(Spike s : spikes){
            g.drawImage(spikeImage, (int)(s.getHitbox().x - xLvlOffset), (int)(s.getHitbox().y - s.getyDrawOffset()),SPIKE_WIDTH, SPIKE_HEIGHT, null);
        }
    }

    private void drawContainers(Graphics g, int xLvlOffset) {
        for (GameContainer gc : containers)
            if (gc.isActive()) {
                int type = 0;
                if (gc.getObjType() == BARREL)
                    type = 1;
                g.drawImage(containerImgs[type][gc.getAniIndex()], (int) (gc.getHitbox().x - gc.getxDrawOffset() - xLvlOffset), (int) (gc.getHitbox().y - gc.getyDrawOffset()), CONTAINER_WIDTH,
                        CONTAINER_HEIGHT, null);
            }
    }

    private void drawPotions(Graphics g, int xLvlOffset) {
        for (Potion p : potions)
            if (p.isActive()) {
                int type = 0;
                if (p.getObjType() == RED_POTION)
                    type = 1;
                g.drawImage(potionImgs[type][p.getAniIndex()], (int) (p.getHitbox().x - p.getxDrawOffset() - xLvlOffset), (int) (p.getHitbox().y - p.getyDrawOffset()), POTION_WIDTH, POTION_HEIGHT,
                        null);
            }
    }

    public void resetAllObjects(){

        loadObjects(playing.getLevelManager().getCurrentLevel());
        for (Potion p : potions){
            p.reset();
        }

        for (GameContainer gc : containers){
            gc.reset();
        }

        for(Cannon c : cannons){
            c.reset();
        }
    }
}
