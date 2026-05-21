package ulb.common;

import ulb.models.run.RunBugemon;

public record LevelUpResult(RunBugemon bugemon, int levelPassed) {
}
