package Main;

public class Game implements Runnable {
    private GameWindow gameWindow;
    private GamePanel gamePanel;
    private Thread gameThread;
    private final int FPS_SET = 120; //frames per sec, draws the game scence (level, players, enemies)
    private final int UPS_SET = 200; //updates per sec, takes care of all the game logic (Move player, events etc)
    public Game() {
        gamePanel = new GamePanel();
        gameWindow = new GameWindow(gamePanel); //pass in panel to window so can see actual graphics
        gamePanel.requestFocus(); //used to get input
        startGameLoop();
    }

    private void startGameLoop() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void update() {
        gamePanel.updateGame();
    }

    @Override
    public void run() {

        double timePerFrame = 1000000000.0/FPS_SET; //nanoseconds
        double timePerUpdate = 1000000000.0/UPS_SET; //time in between updates

        long previousTime = System.nanoTime();


        int frames = 0;
        int updates = 0;
        long lastCheck = System.currentTimeMillis();

        double deltaU = 0; //update
        double deltaF = 0; //frame

        while(true) {
            long currentTime = System.nanoTime();

            deltaU += (currentTime - previousTime) / timePerUpdate; //deltaU will be 1 or more when the duration since the last update is equal or more than timePerUpdate
            deltaU += (currentTime - previousTime) / timePerFrame;
            previousTime = currentTime;

            if(deltaU >=1) {
                update();
                updates++;
                deltaU--;
            }
            if(deltaF >=1){
                gamePanel.repaint();
                frames++;
                deltaF--;
            }


            if (System.currentTimeMillis() - lastCheck >= 1000) {
                lastCheck = System.currentTimeMillis();
                System.out.println("FPS: " + frames + " | UPS: " + updates);
                frames = 0;
                updates = 0;
            }
        }
    }
}