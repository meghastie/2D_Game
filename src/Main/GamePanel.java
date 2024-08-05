package Main;

import Inputs.KeyboardInputs;
import Inputs.MouseInputs;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

/*
Think of JFrame as a picture frame, and JPanel as the picture. Think of 'Graphics' object as a paintbrush.
 */
public class GamePanel extends JPanel {
    private MouseInputs mouseInputs;
    private float xDelta = 100, yDelta = 100;
    private float xDir = 1f, yDir = 1f;
    private int frames = 0;
    private long lastCheck = 0;
    private Color color = new Color(150,20,90);
    private Random random;
    public GamePanel() {
        random = new Random();
        mouseInputs = new MouseInputs(this); //same instance of class for mouse listener and mouse motion listener
        addKeyListener(new KeyboardInputs(this)); //KeyboardInputs extends Key Listener, passes in this class
        addMouseListener(mouseInputs); //deals with when mouse is clicked,pressed,released
        addMouseMotionListener(mouseInputs); // deals with when mouse is moved, dragged (motions)
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

        updateRectangle();
        g.setColor(color);
        g.fillRect((int)xDelta, (int)yDelta, 200, 50);



    }

    public void updateRectangle() {
        xDelta+= xDir;
        if (xDelta > 400 || xDelta < 0) {
            xDir *= -1;
            color = getRndColor();
        }
        yDelta+= yDir;
        if (yDelta > 400 || yDelta < 0) {
            yDir *= -1;
            color = getRndColor();
        }
    }

    private Color getRndColor() {
        int r = random.nextInt(255);
        int g = random.nextInt(255);
        int b = random.nextInt(255);


        return new Color(r,g,b);
    }

}
