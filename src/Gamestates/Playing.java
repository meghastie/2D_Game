package Gamestates;

import Entities.Player;
import Levels.LevelManager;
import Main.Game;
import UI.PauseOverlay;
import Utilz.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/*
Current game scene which we have
 */
public class Playing extends State implements Statemethods{
    private Player player;
    private LevelManager levelManager;
    private PauseOverlay pauseOverlay;
    private boolean paused = false;

    private int xLvlOffset; //offset we will add and remove to draw everything a but to left or right
    private int leftBorder = (int) (0.2 * Game.GAME_WIDTH); //line which if player is beyond, we will calculate if screen has to move. e..g is width is 100px and player is below 20px, we need to move the level to the left
    private int rightBorder = (int) (0.8 * Game.GAME_WIDTH); //same as above but 80%
    private int lvlTilesWide = LoadSave.GetLevelData()[0].length;//HOW MANY TILES WIDE THE WHOLEELEVEL IS - getting image width with .length . used to calulate max offset
    private int maxTilesOffset = lvlTilesWide - Game.TILES_IN_WIDTH; ////need a max val offset can be - dont want to move screen if there is nothing to move to.
    private int maxLvlOffsetX = maxTilesOffset * Game.TILES_SIZE; //turn maxTilesOffset into pixels

    public Playing(Game game) {
        super(game);
        initClasses();
    }

    private void  initClasses() {
        levelManager = new LevelManager(game);
        player = new Player(200, 200, (int) (64 * Game.SCALE), (int) (40 * Game.SCALE));
        player.loadLvlData(levelManager.getCurrentLevel().getLvlData()); //gets lvl data of current lvl. player will now have level data stored
        pauseOverlay = new PauseOverlay(this);
    }

    @Override
    public void update() {
        if(!paused){
            levelManager.update();
            player.update();
            checkCloseToBorder();
        }else{
            pauseOverlay.update();
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
    public void draw(Graphics g) {
        levelManager.draw(g, xLvlOffset);
        player.render(g, xLvlOffset);

        if (paused) {
            g.setColor(new Color(0,0,0,150));
            g.fillRect(0,0,Game.GAME_WIDTH, Game.GAME_HEIGHT);
            pauseOverlay.draw(g);
        }
    }

    /*
    Inputs have been moved over
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {//button1 left, 2 middle, 3 right
            player.setAttacking(true);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(paused){
            pauseOverlay.mouseReleased(e);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if(paused){
            pauseOverlay.mouseMoved(e);
        }
    }


    public void mouseDragged(MouseEvent e){
        if(paused){
            pauseOverlay.mouseDragged(e);
        }
    }



    @Override
    public void mousePressed(MouseEvent e) {
        if(paused){
            pauseOverlay.mousePressed(e);
        }

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            //case KeyEvent.VK_W:
            //  gamePanel.getGame().getPlayer().setUp(true); //move UP
            //break;
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

    @Override
    public void keyReleased(KeyEvent e) {
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

    public void unpauseGame(){
        paused = false;
    }


    public void windowFocusLost(){ //if we lose focus of window (e.g. change window), all direction booleans become false - sprite will stop,
        player.resetDirBooleans();
    }

    public Player getPlayer(){
        return player;
    }
}
