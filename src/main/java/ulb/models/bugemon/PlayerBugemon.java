package ulb.models.bugemon;

import java.util.List;
import java.util.Objects;

public class PlayerBugemon {
    private final Bugemon base;
    private int level;
    private int xp;
    private int bonusHp;
    private int bonusAttack;
    private int bonusDefense;
    private int bonusInitiative;
    private final List<Attack> currentAttacks;

    public PlayerBugemon(Bugemon base, int level, int xp, int bonusHp, int bonusAttack, int bonusDefense,
            int bonusInitiative, List<Attack> currentAttacks) {
        this.base = Objects.requireNonNull(base);
        this.level = level;
        this.xp = xp;
        this.bonusHp = bonusHp;
        this.bonusAttack = bonusAttack;
        this.bonusDefense = bonusDefense;
        this.bonusInitiative = bonusInitiative;

        this.currentAttacks = Objects.requireNonNull(currentAttacks);
        base.checkAttacks(this.currentAttacks);
    }
}
