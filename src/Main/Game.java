package Main;

public class Game {
    private GameWindow gameWindow;
    private GamePanel gamePanel;
    public Game() {
        gamePanel = new GamePanel();
        gameWindow = new GameWindow(gamePanel); //pass in panel to window so ca see actual graphics
    }
}
