package invaders.scene;

import invaders.control.GameKey;
import invaders.encounter.Encounter;
import invaders.encounter.EncounterResult;

import java.util.function.Consumer;

/**
 * Runs any encounter and returns its outcome to the outer flow, without deciding campaign
 * progression.
 */
public final class EncounterScene implements Scene {
    private final Encounter encounter;
    private final Consumer<EncounterResult> onFinished;
    private boolean completionDelivered;

    public EncounterScene(Encounter encounter, Consumer<EncounterResult> onFinished) {
        this.encounter = encounter;
        this.onFinished = onFinished;
    }

    @Override
    public void onEnter() {
        completionDelivered = false;
    }

    @Override
    public void update() {
        if (completionDelivered) return;
        encounter.update();
        encounter
                .result()
                .ifPresent(
                        result -> {
                            completionDelivered = true;
                            onFinished.accept(result);
                        });
    }

    @Override
    public void press(GameKey key) {
        encounter.press(key);
    }

    @Override
    public void release(GameKey key) {
        encounter.release(key);
    }

    @Override
    public void clearInput() {
        encounter.clearInput();
    }
}
