package Main;

import Entities.Player;
import Levels.LevelManager;

import java.awt.*;

/*
The core of the Game. Manages the 2D graphics and updating of game logic.
 */

public class Game implements Runnable {
    private GameWindow gameWindow;
    private GamePanel gamePanel;
    private Thread gameThread;
    private final int FPS_SET = 120; //frames per sec, draws the game scence (level, players, enemies). controls how often the game scene is redrawn
    private final int UPS_SET = 200; //updates per sec, takes care of all the game logic (Move player, events etc). controls how often the game logic is updated
    private Player player;

    private LevelManager levelManager;

    //Calculate the size of the game window based on tile size - keep level in good porprotion to window.
    public final static int TILE_DEFAULT_SIZE = 32;
    public final static float SCALE = 2f;
    public final static int TILES_IN_WIDTH = 26;
    public final static int TILES_IN_HEIGHT = 14;
    public final static int TILES_SIZE = (int) (TILE_DEFAULT_SIZE * SCALE);
    public final static int GAME_WIDTH = TILES_SIZE * TILES_IN_WIDTH;
    public final static int GAME_HEIGHT = TILES_SIZE * TILES_IN_HEIGHT;


    public Game() {
        initClasses(); //have to start before game panel as render is in game panel and player is in render but player is not initialised
        gamePanel = new GamePanel(this);
        gameWindow = new GameWindow(gamePanel); //pass in panel to window so can see actual graphics
        gamePanel.requestFocus(); //used to get input
        startGameLoop();
    }

    private void  initClasses() {
        levelManager = new LevelManager(this);
        player = new Player(200, 200, (int) (64 * SCALE), (int) (40 * SCALE));
        player.loadLvlData(levelManager.getCurrentLevel().getLvlData()); //gets lvl data of current lvl. player will now have level data stored
    }

    private void startGameLoop() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void update() { //updates the game state such as player position and level status
        player.update();
        levelManager.update();
    }

    /*
        constant rendering is essential for smooth motion of animation as objects need to be redrawn at every frame, slightly shifted from their previous position.
        Also, Constant re-rendering ensures that every action by the player is reflected on the screen without delay e.g. pressing buttons, maintaining the responsiveness necessary for an engaging experience
        The game state (positions of objects, health of characters, score, etc.) changes continuously as a result of the game logic. If the game only rendered once and didn't update the display regularly, these changes wouldn't be visible to the player
        In summary, constnat renredering allows the game to accurately reflect real-time changes, player interactions, and environmental updates
         */

    public void render(Graphics g){ //draws the game world, starting with the level and then the player
        levelManager.draw(g);
        player.render(g);
    }

    @Override
    public void run() {

        double timePerFrame = 1000000000.0/FPS_SET; //how long (in nanoseconds) each frame should take based on the desired FPS
        double timePerUpdate = 1000000000.0/UPS_SET; //how long each update should take based on the desired UPS - time in between updates

        long previousTime = System.nanoTime(); //time at strat of the loo[


        int frames = 0;
        int updates = 0;
        long lastCheck = System.currentTimeMillis();

        //accumulators for update and frame timing - a type of register for short-term intermediate cache storage of arithmetic and logic data in a CPU
        double deltaU = 0; //update
        double deltaF = 0; //frame

        while(true) {
            long currentTime = System.nanoTime();

            //track how much time has passed since the last update and frame
            deltaU += (currentTime - previousTime) / timePerUpdate; //deltaU will be 1 or more when the duration since the last update is equal or more than timePerUpdate
            deltaF += (currentTime - previousTime) / timePerFrame;
            previousTime = currentTime;

            if(deltaU >=1) {
                update(); //when enough time has passed to meet the UPS rate, update is called
                updates++; //this handles player movement, enemy behaviour, other game logic
                deltaU--; //decreases to reflect an update has occured
            }
            if(deltaF >=1){
                gamePanel.repaint(); //will trigger render() which draws the current game state to the screen
                frames++;
                deltaF--; //reflects a frame has been rendered
            }


            if (System.currentTimeMillis() - lastCheck >= 1000) {
                lastCheck = System.currentTimeMillis();
                System.out.println("FPS: " + frames + " | UPS: " + updates);
                frames = 0;
                updates = 0;
            }
        }
    }

    public void windowFocusLost(){ //if we lose focus of window (e.g. change window), all direction booleans become false - sprite will stop,
        player.resetDirBooleans();
    }

    public Player getPlayer(){
        return player;
    }

}