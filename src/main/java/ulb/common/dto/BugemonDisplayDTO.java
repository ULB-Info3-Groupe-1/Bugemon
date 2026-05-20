package ulb.common.dto;

import java.util.List;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.ElementType;

public record BugemonDisplayDTO(Bugemon base, int bonusDefense, int bonusAttackPower,
        int bonusInitiative, int bonusMaxHp, int xp, int level, List<Attack> attacks) {

    public int getMaxHp() {
        return this.base().hp() + this.bonusMaxHp();
    }

    public int getDefense() {
        return this.base().defense() + this.bonusDefense();
    }

    public int getAttack() {
        return this.base().attack() + this.bonusAttackPower();
    }

    public int getInitiative() {
        return this.base().initiative() + this.bonusInitiative();
    }

    public String getName() {
        return this.base().name();
    }

    public ElementType getType() {
        return this.base().type();
    }

    public String getSpritePath() {
        return this.base().spritePath();
    }
}
