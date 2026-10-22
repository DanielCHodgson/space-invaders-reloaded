package invaders.audio;

import invaders.asset.SoundId;

/** Audio output port. Headless runs can provide a silent implementation. */
public interface GameAudio extends AutoCloseable {
    void preload();

    void playEffect(SoundId sound);

    void playMusic(SoundId sound);

    @Override
    void close();
}
