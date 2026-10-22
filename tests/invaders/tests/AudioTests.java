package invaders.tests;

import static invaders.tests.TestSupport.*;

import invaders.asset.SoundId;
import invaders.audio.*;
import invaders.event.GameEvent;
import invaders.scene.SceneId;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

public final class AudioTests {
    private AudioTests() {}

    public static void run() throws Exception {
        test(
                "audio load failure is cached and playback remains safe",
                () -> {
                    int[] attempts = {0};
                    AudioPlayer audio =
                            new AudioPlayer(
                                    sound -> {
                                        attempts[0]++;
                                        throw new LineUnavailableException(
                                                "test device unavailable");
                                    });
                    audio.playEffect(SoundId.SHOT);
                    audio.playEffect(SoundId.SHOT);
                    audio.close();
                    audio.playEffect(SoundId.SHOT);
                    check(attempts[0] == 1, "Failed load not retried per shot");
                });
        test(
                "four reusable clips are closed once and never reopened",
                () -> {
                    int[] loads = {0}, starts = {0}, closes = {0};
                    AudioPlayer audio =
                            new AudioPlayer(
                                    sound -> {
                                        loads[0]++;
                                        return (Clip)
                                                Proxy.newProxyInstance(
                                                        AudioTests.class.getClassLoader(),
                                                        new Class<?>[] {Clip.class},
                                                        (proxy, method, args) -> {
                                                            if (method.getName().equals("start"))
                                                                starts[0]++;
                                                            if (method.getName().equals("close"))
                                                                closes[0]++;
                                                            return null;
                                                        });
                                    });
                    audio.preload();
                    audio.playMusic(SoundId.MENU_MUSIC);
                    audio.playMusic(SoundId.MENU_MUSIC);
                    audio.playEffect(SoundId.SHOT);
                    audio.playEffect(SoundId.SHOT);
                    audio.playMusic(SoundId.GAME_MUSIC);
                    check(loads[0] == 4 && starts[0] == 4, "Clip reuse and music debounce");
                    audio.close();
                    audio.close();
                    audio.playEffect(SoundId.EXPLOSION);
                    check(closes[0] == 4 && loads[0] == 4, "Exactly-once shutdown");
                });
        test(
                "audio director maps events without coupling the model to sound files",
                () -> {
                    List<SoundId> music = new ArrayList<>(), effects = new ArrayList<>();
                    GameAudio fake =
                            new GameAudio() {
                                public void preload() {}

                                public void close() {}

                                public void playEffect(SoundId id) {
                                    effects.add(id);
                                }

                                public void playMusic(SoundId id) {
                                    music.add(id);
                                }
                            };
                    AudioDirector director = new AudioDirector(fake);
                    director.update(SceneId.MENU, List.of());
                    director.update(SceneId.PLAYING, List.of(GameEvent.SHOT, GameEvent.EXPLOSION));
                    director.update(SceneId.HELP, List.of());
                    check(
                            music.equals(List.of(SoundId.MENU_MUSIC, SoundId.GAME_MUSIC))
                                    && effects.equals(List.of(SoundId.SHOT, SoundId.EXPLOSION)),
                            "Logical mapping");
                });
    }
}
