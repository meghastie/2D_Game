package Main;

import javax.swing.*;

public class GameWindow {
    private JFrame frame;
    public GameWindow(GamePanel gamePanel) {
        frame = new JFrame();
        frame.setSize(400,400);
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE); //stop running program on close
        frame.add(gamePanel); //should now be able to see actual graphics
        frame.setVisible(true); //should be at end
    }
}
