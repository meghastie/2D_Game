package Gamestates;

import Entities.Player;
import Levels.LevelManager;
import Main.Game;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/*
Current game scene which we have
 */
public class Playing extends State implements Statemethods{
    private Player player;
    private LevelManager levelManager;

    public Playing(Game game) {
        super(game);
        initClasses();
    }

    private void  initClasses() {
        levelManager = new LevelManager(game);
        player = new Player(200, 200, (int) (64 * Game.SCALE), (int) (40 * Game.SCALE));
        player.loadLvlData(levelManager.getCurrentLevel().getLvlData()); //gets lvl data of current lvl. player will now have level data stored
    }

    @Override
    public void update() {
        levelManager.update();
        player.update();
    }

    @Override
    public void draw(Graphics g) {
        levelManager.draw(g);
        player.render(g);
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

    }

    @Override
    public void mouseMoved(MouseEvent e) {

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
            case KeyEvent.VK_BACK_SPACE:
                Gamestate.state = Gamestate.MENU;
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

    public void windowFocusLost(){ //if we lose focus of window (e.g. change window), all direction booleans become false - sprite will stop,
        player.resetDirBooleans();
    }

    public Player getPlayer(){
        return player;
    }
}
