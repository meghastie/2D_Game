package Gamestates;

import Entities.EnemyManager;
import Entities.Player;
import Levels.LevelManager;
import Main.Game;
import Objects.ObjectManager;
import UI.GameOverOverlay;
import UI.LevelCompletedOverlay;
import UI.PauseOverlay;
import Utilz.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import static Utilz.Constants.Environment.*;

/*
Current game scene which we have
 */
public class Playing extends State implements Statemethods{
    private Player player;
    private LevelManager levelManager;
    private EnemyManager enemyManager;
    private PauseOverlay pauseOverlay;
    private GameOverOverlay gameOverOverlay;
    private LevelCompletedOverlay levelCompletedOverlay;
    private ObjectManager objectManager;
    private boolean paused = false;
    private boolean playerDying = false;

    private int xLvlOffset; //offset we will add and remove to draw everything a but to left or right
    private int leftBorder = (int) (0.2 * Game.GAME_WIDTH); //line which if player is beyond, we will calculate if screen has to move. e..g is width is 100px and player is below 20px, we need to move the level to the left
    private int rightBorder = (int) (0.8 * Game.GAME_WIDTH); //same as above but 80%
    private int maxLvlOffsetX ;

    private BufferedImage backgroundImg, bigCloud, smallCloud;
    private int[] smallCloudsPos;
    private Random rnd = new Random();

    private boolean gameOver = false;
    private boolean lvlCompleted;

    public Playing(Game game) {
        super(game);
        initClasses();

        backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BG_IMG);
        bigCloud = LoadSave.GetSpriteAtlas(LoadSave.BIG_CLOUDS);
        smallCloud = LoadSave.GetSpriteAtlas(LoadSave.SMALL_CLOUDS);
        smallCloudsPos = new int[8];
        for(int i = 0; i < smallCloudsPos.length; i++){ //place the small clouds at random intervals
            smallCloudsPos[i] = (int)(90*Game.SCALE) + rnd.nextInt((int)(100*Game.SCALE));//rnd nos between 0-200 (150*2)
        }
        calcLvlOffset();
        loadStartLevel();
    }

    public void loadNextLevel(){
        levelManager.loadNextLevel();
        player.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());
        resetAll();
    }

    private void loadStartLevel() {
        enemyManager.loadEnemies(levelManager.getCurrentLevel());
        objectManager.loadObjects(levelManager.getCurrentLevel()); //if dont call load objetcs from start, have no bojects as they arent initalized in objectManage
    }

    private void calcLvlOffset() {
        maxLvlOffsetX = levelManager.getCurrentLevel().getMaxLvlOffsetX();
    }

    private void  initClasses() {
        levelManager = new LevelManager(game);
        enemyManager = new EnemyManager(this); //ask for playing class and not the game class
        objectManager = new ObjectManager(this);

        player = new Player(200, 200, (int) (64 * Game.SCALE), (int) (40 * Game.SCALE), this);
        player.loadLvlData(levelManager.getCurrentLevel().getLvlData()); //gets lvl data of current lvl. player will now have level data stored
        player.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());

        pauseOverlay = new PauseOverlay(this);
        gameOverOverlay = new GameOverOverlay(this);
        levelCompletedOverlay = new LevelCompletedOverlay(this);
    }

    @Override
    public void update() {

        if(paused){
            pauseOverlay.update();
        }else if(lvlCompleted){
            levelCompletedOverlay.update();
        }else if(gameOver){
            gameOverOverlay.update();
        }else if(playerDying){
            player.update();;
        } else{
            levelManager.update();
            objectManager.update(levelManager.getCurrentLevel().getLvlData(), player);
            player.update();
            enemyManager.update(levelManager.getCurrentLevel().getLvlData(), player);
            checkCloseToBorder();
        }
    }

    private void checkCloseToBorder() {
        int playerX = (int) player.getHitbox().x; //get player
        int diff = playerX - xLvlOffset; //if its more than right border, have to move player to rght. vice versa than lesthan left border

        if(diff > rightBorder){
            xLvlOffset += diff - rightBorder; //if player at 85 and offset 0, 85-0 = 85. if (85>80) offest+= 85-80 ---> offset + 5. if havent moved player is 85, offset 5, 80 not > 80.
        } else if(diff < leftBorder){
            xLvlOffset += diff - leftBorder; //player 30 and offset 15. 30 - 15 = 15, if(15<20) 30+= 15 - 20 ---> 30 - 5
        }

        if(xLvlOffset > maxLvlOffsetX){ //ensure it doesnt go beyond bounds of level
            xLvlOffset = maxLvlOffsetX;
        } else if(xLvlOffset < 0){
            xLvlOffset = 0;
        }
    }

    @Override
    public void  draw(Graphics g) {
        g.drawImage(backgroundImg, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
        drawClouds(g);
        
        levelManager.draw(g, xLvlOffset);
        player.render(g, xLvlOffset);
        enemyManager.draw(g, xLvlOffset);
        objectManager.draw(g, xLvlOffset);

        if (paused) {
            g.setColor(new Color(0,0,0,150));
            g.fillRect(0,0,Game.GAME_WIDTH, Game.GAME_HEIGHT);
            pauseOverlay.draw(g);
        }else if(gameOver){
            gameOverOverlay.draw(g);
        } else if(lvlCompleted){
            levelCompletedOverlay.draw(g);
        }
    }

    private void drawClouds(Graphics g) {

        for (int i = 0; i<3; i++){
            g.drawImage(bigCloud, i * BIG_CLOUD_WIDTH - (int)(xLvlOffset * 0.3), (int)(204 * Game.SCALE), BIG_CLOUD_WIDTH, BIG_CLOUD_HEIGHT, null); //- (int)(xLvlOffset * 0.3) allows the clouds to move
        }

        for (int i = 0; i<3; i++){
            g.drawImage(smallCloud, SMALL_CLOUD_WIDTH * 4 * i - (int)(xLvlOffset * 0.7), smallCloudsPos[i], SMALL_CLOUD_WIDTH, SMALL_CLOUD_HEIGHT, null);
        }

    }

    public void resetAll(){
        gameOver = false;
        paused = false;
        lvlCompleted = false;
        playerDying = false;
        player.resetAll();
        enemyManager.resetAllEnemies();
        lvlCompleted = false;
        objectManager.resetAllObjects();
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public void checkObjectHit(Rectangle2D.Float attackBox){
        objectManager.checkObjectHit(attackBox);
    }

    public void checkSpikesTouched(Player p){
        objectManager.checkSpikesTouched(p);
    }

    public void checkEnemyHit(Rectangle2D.Float attackBox){
        enemyManager.checkEnemyHit(attackBox);
    }

    public void checkPotionTouched(Rectangle2D.Float hitbox){
        objectManager.checkObjectTouched(hitbox);
    }

    /*
    Inputs have been moved over
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if(!gameOver){
            if (e.getButton() == MouseEvent.BUTTON1) {//button1 left, 2 middle, 3 right
                player.setAttacking(true);
                //player.powerAttack();
            }

            //if(e.getButton() == MouseEvent.BUTTON3){ //button 3 right
                //player.powerAttack();
            //}
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(!gameOver){
            if(paused){
                pauseOverlay.mouseReleased(e);
            }else if(lvlCompleted){
                levelCompletedOverlay.mouseReleased(e);}
        }else {
                gameOverOverlay.mouseReleased(e);
            }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if(!gameOver){
            if(paused){
                pauseOverlay.mouseMoved(e);
            }else if(lvlCompleted){
                levelCompletedOverlay.mouseMoved(e);
            }
        }else {
            gameOverOverlay.mouseMoved(e);
        }
    }


    public void mouseDragged(MouseEvent e){
        if(!gameOver){
            if(paused){
                pauseOverlay.mouseDragged(e);
            }
        }
    }



    @Override
    public void mousePressed(MouseEvent e) {
        if (!gameOver) {
            if (paused) {
                pauseOverlay.mousePressed(e);
            } else if (lvlCompleted) {
                levelCompletedOverlay.mousePressed(e);
            }
        } else {
                gameOverOverlay.mousePressed(e);
            }
    }


    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver){
            gameOverOverlay.keyPressed(e);
        }else {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_BACK_SPACE:
                    player.powerAttack();
                    break;
                case KeyEvent.VK_A:
                    player.setLeft(true); //move LEFT
                    break;
                // case KeyEvent.VK_S:
                //   gamePanel.getGame().getPlayer().setDown(true); //move DOWN
                // break;
                case KeyEvent.VK_D:
                    player.setRight(true); //move RIGHT
                    break;
                case KeyEvent.VK_SPACE:
                    player.setJump(true); //jump
                    break;
                case KeyEvent.VK_ESCAPE:
                    paused = !paused;
                    break;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(!gameOver){
            switch (e.getKeyCode()) {
                //case KeyEvent.VK_W:
                //  gamePanel.getGame().getPlayer().setUp(false); //UP
                //break;
                case KeyEvent.VK_A:
                    player.setLeft(false); //LEFT
                    break;
                //case KeyEvent.VK_S:
                //  gamePanel.getGame().getPlayer().setDown(false); //DOWN
                //break;
                case KeyEvent.VK_D:
                    player.setRight(false); //RIGHT
                    break;
                case KeyEvent.VK_SPACE:
                    player.setJump(false);
                    break;
            }
        }
    }

    public void setLvlCompleted(boolean levelCompleted){
        this.lvlCompleted = levelCompleted;
        if(levelCompleted){
            game.getAudioPlayer().lvlCompleted();
        }
    }


    public void setMaxLvlOffset(int lvlOffset){
        this.maxLvlOffsetX = lvlOffset;
    }

    public void unpauseGame(){
        paused = false;
    }


    public void windowFocusLost(){ //if we lose focus of window (e.g. change window), all direction booleans become false - sprite will stop,
        player.resetDirBooleans();
    }

    public Player getPlayer(){
        return player;
    }

    public EnemyManager getEnemyManager() {
        return enemyManager;
    }

    public ObjectManager getObjectManager(){
        return objectManager;
    }

    public LevelManager getLevelManager(){return levelManager;}


    public void setPlayerDying(boolean playerDying) {
        this.playerDying = playerDying;
    }
}
