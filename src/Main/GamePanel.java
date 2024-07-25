package Main;

import javax.swing.*;
import java.awt.*;

/*
Think of JFrame as a picture frame, and JPanel as the picture. Think of 'Graphics' object as a paintbrush.
 */
public class GamePanel extends JPanel {

    public GamePanel() {
    }

    @Override //this method will just make an empty jpanel, must override to draw something on the pannel
    public void paintComponent(Graphics g){
        super.paintComponent(g); //erase everything on previous frame to prevent glitching etc, must always call

        g.fillRect(100, 100, 200, 50);
    }

}
