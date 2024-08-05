package Main;

import Inputs.KeyboardInputs;
import Inputs.MouseInputs;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;


/*
Think of JFrame as a picture frame, and JPanel as the picture. Think of 'Graphics' object as a paintbrush.
 */
public class GamePanel extends JPanel {
    private MouseInputs mouseInputs;
    private float xDelta = 100, yDelta = 100;
    private BufferedImage img, subImg;


    public GamePanel() {
        mouseInputs = new MouseInputs(this); //same instance of class for mouse listener and mouse motion listener
        importImg();
        addKeyListener(new KeyboardInputs(this)); //KeyboardInputs extends Key Listener, passes in this class
        setPanelSize(); //set the size of the game window
        addMouseListener(mouseInputs); //deals with when mouse is clicked,pressed,released
        addMouseMotionListener(mouseInputs); // deals with when mouse is moved, dragged (motions)
    }

    private void importImg() {
        InputStream is = getClass().getResourceAsStream("/player_sprites.png"); //import the image from res, the / tells the game that the image can be found IN one of the folders, not next to it

        try {
            img = ImageIO.read(is);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /*
    had 400x400 in gameWindow, this included border and top bar so playable game window was smaller. jframe will now adjust to size set in panel
     */
    private void setPanelSize() {
        Dimension size = new Dimension(1280,800); //using images that are 32x32
        setMinimumSize(size);
        setPreferredSize(size);
        setMaximumSize(size);
    }

    /*
        allows WASD keys to change pos of rect
    */
    public void changeXDelta(int val) {
        this.xDelta += val;

    }

    /*
        allows WASD keys to change pos of rect
    */
    public void changeYDelta(int val) {
        this.yDelta += val;

    }

    /*
        rectangle moves with mouse when in window
    */
    public void setRecPos(int x, int y) {
        this.xDelta = x;
        this.yDelta = y;

    }

    @Override //this method will just make an empty jpanel, must override to draw something on the pannel
    public void paintComponent(Graphics g){
        super.paintComponent(g); //erase everything on previous frame to prevent glitching etc, must always call

        subImg = img.getSubimage(1*64,8*40,64,40); //get bottom middle sprite in sprite atlas, each sprite is 64x40 and y axis is 1-8
        g.drawImage(subImg, (int)xDelta, (int)yDelta, 128, 80, null); //xDelta and yDelta allows us to controlmsprite, have to cast to int
    }



}