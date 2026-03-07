package ulb.utils;

import java.util.ArrayList;
import java.util.List;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Effect;
import ulb.models.bugemon.EffectType;
import ulb.models.bugemon_team.BugemonTeam;

public final class TestUtilsBugemons {

    private TestUtilsBugemons() {}

    public static Bugemon createDefaultBugemon(String id) {
        Effect effect = new Effect(
            EffectType.STAT_MODIFIER,
            "TestEffect",
            "Flora",
            10,
            "1 turn"
        );
        List<Effect> effects = new ArrayList<>();
        effects.add(effect);
        Attack attack1 = new Attack(
            "TestAttack1",
            "TestAttack1",
            Bugemon.BType.FLORA,
            "",
            30,
            effects
        );
        Attack attack2 = new Attack(
            "TestAttack2",
            "TestAttack2",
            Bugemon.BType.FLORA,
            "",
            20,
            effects
        );
        List<Attack> attackList = List.of(attack1, attack2);

        Bugemon bugemon = new Bugemon.Builder()
            .id(id)
            .name("TestBugemon_" + id)
            .hp(100)
            .attack(20)
            .defense(10)
            .initiative(5)
            .attackList(attackList)
            .isStarter(false)
            .build();

        return bugemon;
    }

    public static List<Bugemon> createDefaultBugemons(int count) {
        List<Bugemon> bugemons = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            bugemons.add(createDefaultBugemon(String.valueOf(i)));
        }
        return bugemons;
    }

    public static BugemonTeam createDefaultTeam(int size) {
        BugemonTeam team = new BugemonTeam();
        for (Bugemon bugemon : createDefaultBugemons(size)) {
            team.addBugemon(bugemon);
        }
        return team;
    }
}
