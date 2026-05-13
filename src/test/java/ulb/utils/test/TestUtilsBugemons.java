package ulb.utils.test;

import java.util.List;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonBuilder;
import ulb.models.bugemon.BugemonType;
import ulb.models.bugemon.effect.Effect;
import ulb.models.bugemon_team.Team;

public final class TestUtilsBugemons {
    private TestUtilsBugemons() {
    }

    private static final List<Effect> NO_EFFECTS = List.of();

    public static Attack createAttack(String id, BugemonType type, int power) {
        return new Attack(id, id, type, "", power, NO_EFFECTS);
    }

    /**
     * Default 3-attacks move-set for tests.
     *
     * <p>
     * Bugemons must have exactly 3 attacks; keep this helper as the single source of truth for most tests.
     */
    public static List<Attack> createDefaultAttackList(BugemonType type) {
        return List.of(createAttack("TestAttack1", type, 30), createAttack("TestAttack2", type, 20),
                createAttack("TestAttack3", type, 10));
    }

    public static Bugemon createDefaultBugemon(String name) {
        List<Attack> attackList = createDefaultAttackList(BugemonType.FLORA);
        return new BugemonBuilder().name(name).hp(100).attack(20).defense(10).initiative(5).attackList(attackList)
                .isStarter(false).build();
    }

    public static Team createDefaultTeam(int count) {
        Team bugemons = new Team();

        for (int i = 1; i <= count; i++) {
            bugemons.add(createDefaultBugemon(String.valueOf(i)));
        }
        return bugemons;
    }

    public static void killBugemon(Team team, String name) {
        Bugemon bugemon = team.stream().filter(b -> b.getName().equals(name)).findFirst().get();

        bugemon.kill();
    }
}
