package invaders.encounter;

import invaders.control.GameControls;
import invaders.event.GameEvent;

import java.util.List;
import java.util.Optional;

/**
 * Lifecycle and outcome port for combat/objective modes. Campaign state belongs outside an
 * encounter.
 */
public interface Encounter extends GameControls {
    void start();

    void update();

    boolean isFinished();

    Optional<EncounterResult> result();

    List<GameEvent> drainEvents();
}
