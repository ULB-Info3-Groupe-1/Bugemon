package ulb.models.combat;

import java.util.List;

import ulb.models.level_up.LevelUp;

public interface ICombatXpDistributor {

    List<LevelUp> distributeXp(CombatContext combatCtx);

}
