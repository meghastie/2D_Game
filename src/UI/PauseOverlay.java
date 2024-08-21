package UI;

import Gamestates.Gamestate;
import Gamestates.Playing;
import Main.Game;
import Utilz.Constants;
import Utilz.LoadSave;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static Utilz.Constants.UI.PauseButtons.SOUND_SIZE;
import static Utilz.Constants.UI.URMButtons.URM_SIZE;
import static Utilz.Constants.UI.VolumeButtons.*;


public class PauseOverlay {
    private BufferedImage backgroundImg;
    private int bgX,bgY,bgW,bgH;
    private SoundButton musicButton, sfxButton;
    private UrmButton menuB, replayB, unpauseB;
    private Playing playing;
    private VolumeButton volumeButton;

    public PauseOverlay(Playing playing){
        this.playing = playing;
        loadBackground();
        createSoundButton();
        createUrmButtons();
        createVolumeButton();
    }

    private void createVolumeButton() {
        int vX = (int) (309*Game.SCALE);
        int vY = (int) (278*Game.SCALE);
        volumeButton = new VolumeButton(vX,vY,SLIDER_WIDTH,VOLUME_HEIGHT );

    }

    private void createUrmButtons() {
        int menuX = (int) (313*Game.SCALE); //will have diff x values, but same y value
        int replayX = (int) (387*Game.SCALE);
        int unpauseX = (int) (462*Game.SCALE);
        int bY = (int) (325*Game.SCALE);

        menuB = new UrmButton(menuX, bY, URM_SIZE, URM_SIZE,2);
        replayB = new UrmButton(replayX, bY, URM_SIZE, URM_SIZE,1);
        unpauseB = new UrmButton(unpauseX, bY, URM_SIZE, URM_SIZE,0);
    }

    private void createSoundButton() {
        int soundX = (int)(450 * Game.SCALE);
        int musicY = (int)(140 * Game.SCALE);
        int sfxY = (int)(186 * Game.SCALE);
        musicButton = new SoundButton(soundX,musicY,SOUND_SIZE,SOUND_SIZE);
        sfxButton = new SoundButton(soundX,sfxY,SOUND_SIZE,SOUND_SIZE);
    }

    private void loadBackground() {
        backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.PAUSE_BACKGROUND);
        bgW = (int) (backgroundImg.getWidth() *Game.SCALE);
        bgH = (int) (backgroundImg.getHeight() *Game.SCALE);
        bgX = Game.GAME_WIDTH / 2 - bgW /2;
        bgY = (int) (25 * Game.SCALE);
    }

    public void update(){
        musicButton.update();
        sfxButton.update();

        menuB.update();
        replayB.update();
        unpauseB.update();

        volumeButton.update();
    }

    public void draw(Graphics g){
        //background
        g.drawImage(backgroundImg, bgX, bgY, bgW, bgH, null );

        //sound buttons
        musicButton.draw(g);
        sfxButton.draw(g);

        //urm buttons
        menuB.draw(g);
        replayB.draw(g);
        unpauseB.draw(g);

        //Volume slider
        volumeButton.draw(g);
    }

    public void mouseDragged(MouseEvent e){
        if(volumeButton.isMousePressed()) {
            volumeButton.changeX(e.getX());
        }
    }


    public void mouseReleased(MouseEvent e) {
        if (isin(e, musicButton)) {
            if (musicButton.isMousePressed()) {
                musicButton.setMuted((!musicButton.isMuted()));
            }
        } else if (isin(e, sfxButton)) {
            if (sfxButton.isMousePressed()) {
                sfxButton.setMuted((!sfxButton.isMuted()));
            }
        }else if (isin(e, menuB)) {
            if (menuB.isMousePressed()) {
                Gamestate.state = Gamestate.MENU;
                playing.unpauseGame();
            }
        }else if (isin(e, replayB)) {
            if (replayB.isMousePressed()) {
                playing.resetAll();
                playing.unpauseGame();
            }
        }else if (isin(e, unpauseB)) {
            if (unpauseB.isMousePressed()) {
                playing.unpauseGame();
            }

        }

        musicButton.resetBools();
        sfxButton.resetBools();
        menuB.resetBools();
        replayB.resetBools();
        unpauseB.resetBools();
    }


    public void mouseMoved(MouseEvent e) {
        musicButton.setMouseOver(false);
        sfxButton.setMouseOver(false);
        menuB.setMouseOver(false);
        replayB.setMouseOver(false);
        unpauseB.setMouseOver(false);

        if(isin(e,musicButton)){
            musicButton.setMouseOver(true);
        }else if (isin(e,sfxButton)){
            sfxButton.setMouseOver(true);
        }else if (isin(e,menuB)) {
            menuB.setMouseOver(true);
        }else if (isin(e,replayB)) {
            replayB.setMouseOver(true);
        }else if (isin(e,unpauseB)) {
            unpauseB.setMouseOver(true);
        }else if (isin(e,volumeButton)) {
            volumeButton.setMouseOver(true);
    }
    }


    public void mousePressed(MouseEvent e) {
        if(isin(e,musicButton)){
            musicButton.setMousePressed(true);
        } else if (isin(e,sfxButton)){
            sfxButton.setMousePressed(true);
        } else if (isin(e,menuB)){
            menuB.setMousePressed(true);
        }else if (isin(e,replayB)) {
            replayB.setMousePressed(true);
        }else if (isin(e,unpauseB)) {
            unpauseB.setMousePressed(true);
        }else if (isin(e,volumeButton)) {
            volumeButton.setMousePressed(true);
        }

    }

    private boolean isin(MouseEvent e, PauseButton b){
        return b.getBounds().contains(e.getX(), e.getY());
    }
}
