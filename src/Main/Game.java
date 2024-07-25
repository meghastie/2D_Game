package Main;

public class Game {
    private GameWindow gameWindow;
    private GamePanel gamePanel;
    public Game() {
        gamePanel = new GamePanel();
        gameWindow = new GameWindow(gamePanel); //pass in panel to window so can see actual graphics
        gamePanel.requestFocus(); //used to get input
    }
}
