package ulb.utils;

import java.util.ArrayList;
import java.util.List;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.AttackList;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Effect;
import ulb.models.bugemon.Stats;
import ulb.models.bugemon.Type;

public final class TestUtilsBugemons {

    private TestUtilsBugemons() {
    }

    public static Bugemon createDefaultBugemon(String id) {
        Stats stats = new Stats(100, 20, 10, 5);
        Effect effect = new Effect("TypeEffect", "TestEffect", "Flora", 10,
                "1 turn");
        List<Effect> effects = new ArrayList<>();
        effects.add(effect);
        Attack attack1 = new Attack("TestAttack1", "TestAttack1", Type.FLORA, "", 30, effects);
        Attack attack2 = new Attack("TestAttack2", "TestAttack2", Type.FLORA, "", 20, effects);
        AttackList attackList = new AttackList(List.of(attack1, attack2));
        return new Bugemon(id, "TestBugemon_" + id, Type.FLORA, "TestSprite", stats, attackList, false);
    }
}
