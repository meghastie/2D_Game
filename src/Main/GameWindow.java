package Main;

import javax.swing.*;

public class GameWindow {
    private JFrame frame;
    public GameWindow(GamePanel gamePanel) {
        frame = new JFrame();
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE); //stop running program on close
        frame.add(gamePanel); //should now be able to see actual graphics
        frame.setLocationRelativeTo(null); //game window now in center
        frame.setResizable(false); //dont allow window to be resized
        frame.pack(); //tell jframe to set size of window to preferred size of components - we only have one, jpanel which has preferred size set in its methods
        frame.setVisible(true); //should be at end
    }
}
