package Main;

import Inputs.KeyboardInputs;
import Inputs.MouseInputs;

import javax.swing.*;
import java.awt.*;

/*
Think of JFrame as a picture frame, and JPanel as the picture. Think of 'Graphics' object as a paintbrush.
 */
public class GamePanel extends JPanel {
    private MouseInputs mouseInputs;
    private int xDelta = 100, yDelta = 100;
    public GamePanel() {
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
        repaint();
    }

    /*
        allows WASD keys to change pos of rect
    */
    public void changeYDelta(int val) {
        this.yDelta += val;
        repaint();
    }

    /*
        rectangle moves with mouse when in window
    */
    public void setRecPos(int x, int y) {
        this.xDelta = x;
        this.yDelta = y;
        repaint();
    }

    @Override //this method will just make an empty jpanel, must override to draw something on the pannel
    public void paintComponent(Graphics g){
        super.paintComponent(g); //erase everything on previous frame to prevent glitching etc, must always call

        g.fillRect(xDelta, yDelta, 200, 50);
    }

}
