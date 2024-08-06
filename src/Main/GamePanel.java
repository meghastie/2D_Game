package Main;

import Inputs.KeyboardInputs;
import Inputs.MouseInputs;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import static Utilz.Constants.PlayerConstants.*;
import static Utilz.Constants.Directions.*;
import static Main.Game.GAME_HEIGHT;
import static Main.Game.GAME_WIDTH;


/*
Think of JFrame as a picture frame, and JPanel as the picture. Think of 'Graphics' object as a paintbrush.
 */
public class GamePanel extends JPanel {
    private MouseInputs mouseInputs;
  private Game game;


    public GamePanel(Game game) {
        mouseInputs = new MouseInputs(this); //same instance of class for mouse listener and mouse motion listener
        this.game = game;

        setPanelSize(); //set the size of the game window
        addKeyListener(new KeyboardInputs(this)); //KeyboardInputs extends Key Listener, passes in this class
        addMouseListener(mouseInputs); //deals with when mouse is clicked,pressed,released
        addMouseMotionListener(mouseInputs); // deals with when mouse is moved, dragged (motions)
    }

    /*
    had 400x400 in gameWindow, this included border and top bar so playable game window was smaller. jframe will now adjust to size set in panel
     */
    private void setPanelSize() {
        Dimension size = new Dimension(GAME_WIDTH, GAME_HEIGHT); //using images that are 32x32
        setPreferredSize(size);
    }

    public void updateGame() {

    }

    @Override //this method will just make an empty jpanel, must override to draw something on the pannel
    public void paintComponent(Graphics g) {
        super.paintComponent(g); //erase everything on previous frame to prevent glitching etc, must always call
        game.render(g);
    }

    public Game getGame() {
        return game;
    }

}