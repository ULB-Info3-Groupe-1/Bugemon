package ulb.utils.test;

import java.util.ArrayList;
import java.util.List;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonBuilder;
import ulb.models.bugemon.BugemonType;
import ulb.models.bugemon.effect.Effect;
import ulb.models.bugemon.effect.EffectStat;
import ulb.models.bugemon.effect.EffectTarget;
import ulb.models.bugemon.effect.EffectType;
import ulb.models.bugemon_team.BugemonTeam;

public final class TestUtilsBugemons {
    private TestUtilsBugemons() {}

    public static Bugemon createDefaultBugemon(String id) {
        Effect effect = new Effect(EffectType.STAT_MODIFIER, EffectTarget.ADVERSARY,
                                   EffectStat.ATTACK, 10, "1_turn");
        List<Effect> effects = new ArrayList<>();
        effects.add(effect);
        Attack attack1 =
                new Attack("TestAttack1", "TestAttack1", BugemonType.FLORA, "", 30, effects);
        Attack attack2 =
                new Attack("TestAttack2", "TestAttack2", BugemonType.FLORA, "", 20, effects);
        List<Attack> attackList = List.of(attack1, attack2);

        Bugemon bugemon = new BugemonBuilder()
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

    public static BugemonTeam createDefaultTeam(int count) {
        BugemonTeam bugemons = new BugemonTeam();

        for (int i = 1; i <= count; i++) {
            bugemons.add(createDefaultBugemon(String.valueOf(i)));
        }

        return bugemons;
    }

    public static void killBugemon(BugemonTeam team, String id) {
        Bugemon bugemon = team.stream().filter(b -> b.getId().equals(id)).findFirst().get();
        bugemon.takeDamage(bugemon.getHp());
    }
}
