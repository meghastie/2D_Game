package Inputs;

import Main.GamePanel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static Utilz.Constants.Directions.*;

public class KeyboardInputs implements KeyListener {

    private GamePanel gamePanel;

    public KeyboardInputs(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }


    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                gamePanel.getGame().getPlayer().setUp(true); //move UP
                break;
            case KeyEvent.VK_A:
                gamePanel.getGame().getPlayer().setLeft(true); //move LEFT
                break;
            case KeyEvent.VK_S:
                gamePanel.getGame().getPlayer().setDown(true); //move DOWN
                break;
            case KeyEvent.VK_D:
                gamePanel.getGame().getPlayer().setRight(true); //move RIGHT
                break;
        }
    }
        @Override
        public void keyReleased (KeyEvent e){ //WHEN RELSEASE KEY , NO DIRECTIONS, SO STOP
            switch (e.getKeyCode()) {
                case KeyEvent.VK_W:
                    gamePanel.getGame().getPlayer().setUp(false); //UP
                    break;
                case KeyEvent.VK_A:
                    gamePanel.getGame().getPlayer().setLeft(false); //LEFT
                    break;
                case KeyEvent.VK_S:
                    gamePanel.getGame().getPlayer().setDown(false); //DOWN
                    break;
                case KeyEvent.VK_D:
                    gamePanel.getGame().getPlayer().setRight(false); //RIGHT
                    break;
            }
    }
}

