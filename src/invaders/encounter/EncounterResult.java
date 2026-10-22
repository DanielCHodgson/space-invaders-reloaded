package invaders.encounter;

/** Data returned to an outer campaign/objective controller, independent of scenes or artwork. */
public record EncounterResult(EncounterOutcome outcome, int score) {}
