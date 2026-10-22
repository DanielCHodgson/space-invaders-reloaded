package invaders.audio;

import invaders.asset.SoundId;

import java.io.IOException;

import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

@FunctionalInterface
public interface ClipLoader {
    Clip open(SoundId sound)
            throws IOException, UnsupportedAudioFileException, LineUnavailableException;
}
