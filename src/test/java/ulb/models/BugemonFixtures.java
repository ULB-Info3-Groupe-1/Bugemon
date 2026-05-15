package ulb.models;

import java.util.List;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonType;
import ulb.models.combat.CombatBugemon;
import ulb.models.combat.CombatTeam;
import ulb.models.player.PlayerBugemon;
import ulb.models.run.RunBugemon;

public final class BugemonFixtures {

    private BugemonFixtures() {
    }

    public static Attack floraAttack() {
        return new Attack("atk-flora", "Fouet-Liane", "", 40, BugemonType.FLORA, List.of());
    }

    public static Attack aquaAttack() {
        return new Attack("atk-aqua", "Jet d'Eau", "", 35, BugemonType.AQUA, List.of());
    }

    public static Bugemon fastFlora() {
        return new Bugemon("flora-1", "FloraFast", 100, 50, 40, 70,
                BugemonType.FLORA, List.of(floraAttack(), floraAttack(), floraAttack()), "", false);
    }

    public static Bugemon slowAqua() {
        return new Bugemon("aqua-1", "AquaSlow", 100, 50, 40, 30,
                BugemonType.AQUA, List.of(aquaAttack(), aquaAttack(), aquaAttack()), "", false);
    }

    public static CombatTeam teamOf(Bugemon bugemon) {
        return new CombatTeam(List.of(
                new CombatBugemon(new RunBugemon(new PlayerBugemon(bugemon)))));
    }
}
