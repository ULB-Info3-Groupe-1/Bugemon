package ulb.utils;

import java.util.ArrayList;
import java.util.List;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.AttackList;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Effect;
import ulb.models.bugemon.Stats;
import ulb.models.bugemon_team.BugemonTeam;


public final class TestUtilsBugemons {

    private TestUtilsBugemons() {}

    public static Bugemon createDefaultBugemon(String id) {
        Stats stats = new Stats(100, 20, 10, 5);
        Effect effect = new Effect("TestEffect", "TestEffect", "Flora", 10,
                "1 turn");
        List<Effect> effectList = new ArrayList<>();
        effectList.add(effect);
        Attack attack1 = new Attack("TestAttack1", "TestAttack1", "Flora", "", 30, effectList);
        Attack attack2 = new Attack("TestAttack2", "TestAttack2", "Flora", "", 20, effectList);
        AttackList attackList = new AttackList(List.of(attack1, attack2));
        return new Bugemon(id, "TestBugemon_" + id, Bugemon.Type.FLORA, "TestSprite", stats, attackList, false);
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
