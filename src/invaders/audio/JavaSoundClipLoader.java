package invaders.audio;

import invaders.asset.AssetCatalog;
import invaders.asset.ResourceLoader;
import invaders.asset.SoundId;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.*;

public final class JavaSoundClipLoader implements ClipLoader {
    private final ResourceLoader resources;
    private final AssetCatalog catalog;

    public JavaSoundClipLoader(ResourceLoader resources, AssetCatalog catalog) {
        this.resources = resources;
        this.catalog = catalog;
    }

    @Override
    public Clip open(SoundId sound)
            throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        Clip clip = null;
        try (InputStream file = resources.open(catalog.sound(sound));
                BufferedInputStream buffered = new BufferedInputStream(file);
                AudioInputStream audio = AudioSystem.getAudioInputStream(buffered)) {
            clip = AudioSystem.getClip();
            clip.open(audio);
            return clip;
        } catch (IOException
                | UnsupportedAudioFileException
                | LineUnavailableException
                | RuntimeException e) {
            if (clip != null) clip.close();
            throw e;
        }
    }
}
