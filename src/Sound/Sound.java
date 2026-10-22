package Sound;

import java.applet.AudioClip;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Sound {
    // to store current position
    Long currentFrame;
    Clip clip;

    // current status of clip
    String status;

    AudioInputStream audioInputStream;
    // static String filePath = "src/Music/Menu_Music.au";
    static String filePath;

    // constructor to initialize streams and clip

    public Sound(String filePath)
            throws UnsupportedAudioFileException, IOException, LineUnavailableException {

        this.filePath = filePath;
        // create AudioInputStream object
        audioInputStream = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());

        // create clip reference
        clip = AudioSystem.getClip();

        // open audioInputStream to the clip
        clip.open(audioInputStream);

        // clip.loop(Clip.LOOP_CONTINUOUSLY);

    }

    // Method to play the audio

    public void play() {
        // start the clip
        clip.start();

        status = "play";
    }

    // Method to stop the audio

    public void stop() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        currentFrame = 0L;
        clip.stop();
        clip.close();
    }

    public static void setFilePath(String filePath) {
        Sound.filePath = filePath;
    }
}
