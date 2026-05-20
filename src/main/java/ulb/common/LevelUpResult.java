package ulb.common;

import ulb.models.player.PlayerBugemon;

public record LevelUpResult(PlayerBugemon bugemon, int levelPassed) {
}
