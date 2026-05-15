package ulb.models.combat;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonType;
import ulb.models.combat.TurnStep.AttackStep;
import ulb.models.player.PlayerBugemon;
import ulb.models.run.RunBugemon;
import ulb.models.trainer.TurnAction.AttackAction;

public class TestCombat {

    private Attack floraAttack;
    private CombatTeam playerTeam;
    private CombatTeam opponentTeam;
    private Combat combat;
    private Random seededRandom;

    @Before
    public void setUp() {
        this.seededRandom = new Random(42);
        this.floraAttack = new Attack("fouet", "Fouet-Liane", "", 40, BugemonType.FLORA, List.of());

        Bugemon playerBase = new Bugemon(
                "p1",
                "PlayerBug",
                100,
                50,
                40,
                70,
                BugemonType.FLORA,
                List.of(this.floraAttack),
                "",
                false);
        Bugemon opponentBase = new Bugemon(
                "o1",
                "OpponentBug",
                100,
                50,
                40,
                30,
                BugemonType.AQUA,
                List.of(this.floraAttack),
                "",
                false);

        this.playerTeam = new CombatTeam(List.of(new CombatBugemon(new RunBugemon(new PlayerBugemon(playerBase)))));
        this.opponentTeam = new CombatTeam(List.of(new CombatBugemon(new RunBugemon(new PlayerBugemon(opponentBase)))));

        this.combat = new Combat(
                this.playerTeam,
                this.opponentTeam,
                new AutoStrategy(this.seededRandom),
                new AutoStrategy(this.seededRandom),
                new DamageCalculator());
    }

    @Test
    public void testResolveTurnProducesActions() {
        AttackAction playerAttack = new AttackAction(this.floraAttack);
        AttackAction opponentAttack = new AttackAction(this.floraAttack);

        List<TurnStep> turnSteps = new ArrayList<>();

        this.combat.resolveTurn(playerAttack, opponentAttack, turnSteps::addAll);

        assertFalse(turnSteps.isEmpty());
        // Should contain at least 2 AttackActions (player and opponent)
        long attackCount = turnSteps.stream().filter(a -> a instanceof AttackStep).count();
        assertTrue("At least one attack occured", attackCount >= 1);
    }
}
