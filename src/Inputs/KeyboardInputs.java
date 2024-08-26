package Inputs;

import Gamestates.Gamestate;
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

    /*
    Will now control switching between states.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        switch(Gamestate.state){
            case MENU:
                gamePanel.getGame().getMenu().keyPressed(e); //if key is pressed, go to keyPressed in Menu class. if it is enter, start playing game.
                break;
            case PLAYING:
                gamePanel.getGame().getPlaying().keyPressed(e); //if key is pressed, go to keyPressed in Playing class. if it is backspace, go to menu.
                break;
            case OPTIONS:
                gamePanel.getGame().getGameOptions().keyPressed(e);
            default:
                break;
        }
    }
        @Override
        public void keyReleased (KeyEvent e){ //WHEN RELSEASE KEY , NO DIRECTIONS, SO STOP
            switch(Gamestate.state){
                case MENU:
                    gamePanel.getGame().getMenu().keyReleased(e);
                    break;
                case PLAYING:
                    gamePanel.getGame().getPlaying().keyReleased(e);
                    break;
                case OPTIONS:
                    gamePanel.getGame().getGameOptions().keyReleased(e);
                default:
                    break;
            }
        }
}

