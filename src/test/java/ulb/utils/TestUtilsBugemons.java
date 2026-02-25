package ulb.utils;

import java.util.List;

import ulb.bugemon.models.Attack;
import ulb.bugemon.models.AttackList;
import ulb.bugemon.models.Bugemon;
import ulb.bugemon.models.Stats;

public final class TestUtilsBugemons {

    private TestUtilsBugemons() {}

    public static Bugemon createDefaultBugemon(String id) {
        Stats stats = new Stats(100, 20, 10, 5);
        ulb.bugemon.models.Effect effect = new ulb.bugemon.models.Effect("TestEffect", "TestEffect", "Flora", 10,
                "1 turn");
        Attack attack1 = new Attack("TestAttack1", "TestAttack1", "Flora", "", 30, effect);
        Attack attack2 = new Attack("TestAttack2", "TestAttack2", "Flora", "", 20, effect);
        AttackList attackList = new AttackList(List.of(attack1, attack2));
        return new Bugemon(id, "TestBugemon_" + id, "Flora", "TestSprite", stats, attackList, false);
    }
}
