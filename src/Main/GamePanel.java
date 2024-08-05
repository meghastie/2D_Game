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


/*
Think of JFrame as a picture frame, and JPanel as the picture. Think of 'Graphics' object as a paintbrush.
 */
public class GamePanel extends JPanel {
    private MouseInputs mouseInputs;
    private float xDelta = 100, yDelta = 100;
    private BufferedImage img;
    private BufferedImage[][] animations;
    private int aniTick, aniIndex, aniSpeed = 15; //anispeed - lower anispeed faster animaton will go
    private int playerAction = IDLE;
    private int playerDir = -1; //if not moving, idle so -1. if moving it is o,1,2 or 3 (see Direction class)
    private boolean moving = false;


    public GamePanel() {
        mouseInputs = new MouseInputs(this); //same instance of class for mouse listener and mouse motion listener

        importImg();
        loadAnimations();

        addKeyListener(new KeyboardInputs(this)); //KeyboardInputs extends Key Listener, passes in this class
        setPanelSize(); //set the size of the game window
        addMouseListener(mouseInputs); //deals with when mouse is clicked,pressed,released
        addMouseMotionListener(mouseInputs); // deals with when mouse is moved, dragged (motions)
    }

    private void loadAnimations() {
        animations = new BufferedImage[9][6]; //y is 9 (9 down the way), x is 6 (6 along way) on sprite sheet

        for (int j = 0; j < animations.length; j++) {
            for (int i = 0; i < animations[j].length; i++) {
                animations[j][i] = img.getSubimage(i * 64, j * 40, 64, 40); //each sprite is 64 bits long so 0*64 will get first sprite, 1 *64 will get next, etc. each is 40 long
            }
        }
    }

    private void importImg() {
        InputStream is = getClass().getResourceAsStream("/player_sprites.png"); //import the image from res, the / tells the game that the image can be found IN one of the folders, not next to it

        try {
            img = ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    had 400x400 in gameWindow, this included border and top bar so playable game window was smaller. jframe will now adjust to size set in panel
     */
    private void setPanelSize() {
        Dimension size = new Dimension(1280, 800); //using images that are 32x32
        setMinimumSize(size);
        setPreferredSize(size);
        setMaximumSize(size);
    }

  public void setDirection(int direction){
        this.playerDir = direction;
        moving = true;
  }

  public void setMoving(boolean moving){
        this.moving = moving;
  }

    private void updateAnimationTick() {

        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= GetSpriteAmount(playerAction)) { //before we had this as >= 6, which would cause sprite to glitch as some actions have less than 6 sprite animations
                aniIndex = 0;
            }
        }

    }

    private void setAnimation() {
        if(moving){
            playerAction = RUNNING;
        } else {
            playerAction = IDLE;
        }
    }

    private void updatePos() {
        if(moving){
            switch(playerDir) {
                case LEFT:
                    xDelta-=5;
                    break;
                case UP:
                    yDelta-=5;
                    break;
                case RIGHT:
                    xDelta+=5;
                    break;
                case DOWN:
                    yDelta+=5;
                    break;
            }
        }
    }

    public void updateGame() {
        updateAnimationTick();
        setAnimation();
        updatePos();
    }

    @Override //this method will just make an empty jpanel, must override to draw something on the pannel
    public void paintComponent(Graphics g) {
        super.paintComponent(g); //erase everything on previous frame to prevent glitching etc, must always call

        g.drawImage(animations[playerAction][aniIndex], (int) xDelta, (int) yDelta, 256, 160, null); //xDelta and yDelta allows us to control sprite, have to cast to int


    }


}