package ulb.models.combat.utils;

import ulb.models.combat.CombatTeam;

public record CombatContext(CombatTeam allyTeam, CombatTeam opponentTeam) {
}
