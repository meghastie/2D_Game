package UI;

import Gamestates.Gamestate;
import Main.Game;

import java.awt.*;
import java.awt.event.MouseEvent;

import static Utilz.Constants.UI.PauseButtons.SOUND_SIZE;
import static Utilz.Constants.UI.VolumeButtons.SLIDER_WIDTH;
import static Utilz.Constants.UI.VolumeButtons.VOLUME_HEIGHT;

public class AudioOptions {

    private VolumeButton volumeButton;
    private SoundButton musicButton, sfxButton;

    public AudioOptions(){
        createSoundButton();
        createVolumeButton();
    }

    private void createVolumeButton() {
        int vX = (int) (309* Game.SCALE);
        int vY = (int) (278*Game.SCALE);
        volumeButton = new VolumeButton(vX,vY,SLIDER_WIDTH,VOLUME_HEIGHT );

    }

    private void createSoundButton() {
        int soundX = (int)(450 * Game.SCALE);
        int musicY = (int)(140 * Game.SCALE);
        int sfxY = (int)(186 * Game.SCALE);
        musicButton = new SoundButton(soundX,musicY,SOUND_SIZE,SOUND_SIZE);
        sfxButton = new SoundButton(soundX,sfxY,SOUND_SIZE,SOUND_SIZE);
    }

    public void update(){
        musicButton.update();
        sfxButton.update();
        volumeButton.update();
    }

    public void draw(Graphics g){
        //sound buttons
        musicButton.draw(g);
        sfxButton.draw(g);

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

        }

        musicButton.resetBools();
        sfxButton.resetBools();
    }


    public void mouseMoved(MouseEvent e) {
        musicButton.setMouseOver(false);
        sfxButton.setMouseOver(false);

        if(isin(e,musicButton)){
            musicButton.setMouseOver(true);
        }else if (isin(e,sfxButton)){
            sfxButton.setMouseOver(true);
        }else if (isin(e,volumeButton)) {
            volumeButton.setMouseOver(true);
        }
    }


    public void mousePressed(MouseEvent e) {
        if(isin(e,musicButton)){
            musicButton.setMousePressed(true);
        } else if (isin(e,sfxButton)){
            sfxButton.setMousePressed(true);
        }else if (isin(e,volumeButton)) {
            volumeButton.setMousePressed(true);
        }

    }

    private boolean isin(MouseEvent e, PauseButton b){
        return b.getBounds().contains(e.getX(), e.getY());
    }
}

