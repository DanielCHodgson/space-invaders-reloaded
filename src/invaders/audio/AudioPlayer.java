package invaders.audio;

import invaders.asset.SoundId;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

import javax.sound.sampled.*;

public final class AudioPlayer implements GameAudio {
    private final ClipLoader loader;
    private final Map<SoundId, Clip> clips = new EnumMap<>(SoundId.class);
    private boolean closed;
    private SoundId music;

    public AudioPlayer(ClipLoader loader) {
        this.loader = loader;
    }

    private Clip load(SoundId sound) {
        if (closed) return null;
        if (clips.containsKey(sound)) return clips.get(sound);
        Clip clip = null;
        try {
            clip = loader.open(sound);
        } catch (IOException
                | UnsupportedAudioFileException
                | LineUnavailableException
                | IllegalArgumentException
                | SecurityException e) {
            System.err.println("Audio unavailable (" + sound + "): " + e.getMessage());
        }
        clips.put(sound, clip);
        return clip;
    }

    @Override
    public void preload() {
        for (SoundId sound : SoundId.values()) load(sound);
    }

    @Override
    public void playEffect(SoundId sound) {
        Clip clip = load(sound);
        if (clip != null) {
            clip.stop();
            clip.setFramePosition(0);
            clip.start();
        }
    }

    @Override
    public void playMusic(SoundId sound) {
        if (closed || sound == music) return;
        Clip previous = clips.get(music);
        if (previous != null) previous.stop();
        music = sound;
        Clip clip = load(sound);
        if (clip != null) {
            clip.setFramePosition(0);
            clip.start();
        }
    }

    @Override
    public void close() {
        if (closed) return;
        closed = true;
        clips.values().stream().filter(java.util.Objects::nonNull).forEach(Clip::close);
        clips.clear();
    }
}
