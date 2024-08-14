package UI;

import Main.Game;
import Utilz.LoadSave;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static Utilz.Constants.UI.SOUND_SIZE;

public class PauseOverlay {
    private BufferedImage backgroundImg;
    private int bgX,bgY,bgW,bgH;
    private SoundButton musicButton, sfxButton;

    public PauseOverlay(){
        loadBackground();
        createSoundButton();
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
    }

    public void draw(Graphics g){
        //background
        g.drawImage(backgroundImg, bgX, bgY, bgW, bgH, null );

        //sound buttons
        musicButton.draw(g);
        sfxButton.draw(g);
    }

    public void mouseDragged(){

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

        }
    }


    public void mouseMoved(MouseEvent e) {
        musicButton.setMouseOver(false);
        sfxButton.setMouseOver(false);

        if(isin(e,musicButton)){
            musicButton.setMousePressed(true);
        } else if (isin(e,sfxButton)){
            sfxButton.setMousePressed(true);
        }
    }


    public void mousePressed(MouseEvent e) {
        if(isin(e,musicButton)){
            musicButton.setMousePressed(true);
        } else if (isin(e,sfxButton)){
            sfxButton.setMousePressed(true);
        }
    }

    private boolean isin(MouseEvent e, PauseButton b){
        return b.getBounds().contains(e.getX(), e.getY());
    }
}
