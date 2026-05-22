package ulb.common;

import ulb.models.run.RunBugemon;

/**
 * Immutable result of a level-up event, pairing the {@link RunBugemon} that levelled up with the new level it reached.
 *
 * @param bugemon
 *            the Bugemon instance that gained the level
 * @param levelPassed
 *            the new level reached after the experience gain
 */
public record LevelUpResult(RunBugemon bugemon, int levelPassed) {
}
