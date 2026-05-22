package ulb.common.dto.persistence;

import java.util.List;

import ulb.models.bugemon.Attack;

public record PlayerBugemonDTO(String playername, String bugemonName, int bonusDefense, int bonusAttackPower,
        int bonusInitiative, int bonusMaxHp, int xp, int level, List<Attack> attacks) {
}
