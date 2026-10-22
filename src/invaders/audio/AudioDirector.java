package invaders.audio;

import invaders.asset.SoundId;
import invaders.event.GameEvent;
import invaders.scene.SceneId;

import java.util.List;

/** Maps scene changes and domain events to logical sounds; the model never plays audio. */
public final class AudioDirector {
    private final GameAudio output;

    public AudioDirector(GameAudio output) {
        this.output = output;
    }

    public void update(SceneId scene, List<GameEvent> events) {
        if (scene == SceneId.MENU) output.playMusic(SoundId.MENU_MUSIC);
        else if (scene != SceneId.HELP) output.playMusic(SoundId.GAME_MUSIC);
        for (GameEvent event : events)
            output.playEffect(
                    switch (event) {
                        case SHOT -> SoundId.SHOT;
                        case EXPLOSION -> SoundId.EXPLOSION;
                    });
    }
}
