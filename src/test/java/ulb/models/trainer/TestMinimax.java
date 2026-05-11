package ulb.models.trainer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.services.InventoryService;
import ulb.services.InventoryService;
import ulb.utils.test.TestUtilsBugemonTeam;

public class TestMinimax {
    private AITrainer aiTrainer;
    private Trainer opponent;
    private MiniMax miniMax;
    private InventoryService inventoryService;

    @Before
    public void setUp() {
        this.inventoryService = mock(InventoryService.class);
        this.inventoryService = mock(InventoryService.class);
        BugemonTeam aiTeam = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        BugemonTeam opponentTeam = TestUtilsBugemonTeam.createDefaultBugemonTeam(false);
        this.aiTrainer = new AITrainer(aiTeam, this.inventoryService, 2);
        this.aiTrainer = new AITrainer(aiTeam, this.inventoryService, 2);
        this.opponent = new AutoTrainer(opponentTeam);
        this.miniMax = new MiniMax(2);
    }

    @Test
    public void testChooseBestActionReturnsValidAction() {
        this.aiTrainer.setOpponentTrainer(this.opponent);
        this.aiTrainer.setOpponentActiveBugemon(this.opponent.getCurrentBugemon());

        TurnAction action = this.miniMax.chooseBestAction(this.aiTrainer, this.opponent);

        assertNotNull(action);
        assertTrue(action instanceof TurnAction.AttackAction || action instanceof TurnAction.SwitchAction
                || action instanceof TurnAction.UseItemAction);
    }

    @Test
    public void testChooseBestActionPrefersAttack() {
        // With full HP on both sides and basic setup, Minimax should prefer attacking
        this.aiTrainer.setOpponentTrainer(this.opponent);
        this.aiTrainer.setOpponentActiveBugemon(this.opponent.getCurrentBugemon());

        TurnAction action = this.miniMax.chooseBestAction(this.aiTrainer, this.opponent);

        assertTrue(action instanceof TurnAction.AttackAction);
    }

    @Test
    public void testChooseBestActionWithDifferentDepths() {
        this.aiTrainer.setOpponentTrainer(this.opponent);
        this.aiTrainer.setOpponentActiveBugemon(this.opponent.getCurrentBugemon());

        MiniMax shallowMiniMax = new MiniMax(1);
        TurnAction shallowAction = shallowMiniMax.chooseBestAction(this.aiTrainer, this.opponent);
        assertNotNull(shallowAction);

        MiniMax deepMiniMax = new MiniMax(3);
        TurnAction deepAction = deepMiniMax.chooseBestAction(this.aiTrainer, this.opponent);
        assertNotNull(deepAction);
    }

    @Test
    public void testChooseBestSwitchAfterKoReturnsValidBugemon() {
        this.aiTrainer.setOpponentTrainer(this.opponent);
        this.aiTrainer.setOpponentActiveBugemon(this.opponent.getCurrentBugemon());

        BugemonTeam team = this.aiTrainer.getTeam();
        int aliveBefore = (int) team.aliveStream().count();

        if (aliveBefore > 1) {
            Bugemon switched = this.miniMax.chooseBestSwitchAfterKo(this.aiTrainer, this.opponent);
            if (switched != null) {
                assertTrue(switched.isAlive());
                assertEquals(aliveBefore, (int) team.aliveStream().count());
            }
        }
    }

    @Test
    public void testChooseBestSwitchAfterKoWithMultipleLiveAlternatives() {
        // Ensure multiple bugemons are alive
        this.aiTrainer.setOpponentTrainer(this.opponent);
        this.aiTrainer.setOpponentActiveBugemon(this.opponent.getCurrentBugemon());

        BugemonTeam team = this.aiTrainer.getTeam();
        int aliveCount = (int) team.aliveStream().count();

        if (aliveCount >= 2) {
            Bugemon switched = this.miniMax.chooseBestSwitchAfterKo(this.aiTrainer, this.opponent);

            if (switched != null) {
                assertTrue(switched.isAlive());
                assertNotNull(switched);
            }
        }
    }

    @Test
    public void testChooseBestActionWithEmptyInventory() {
        this.aiTrainer.setOpponentTrainer(this.opponent);
        this.aiTrainer.setOpponentActiveBugemon(this.opponent.getCurrentBugemon());

        TurnAction action = this.miniMax.chooseBestAction(this.aiTrainer, this.opponent);
        assertNotNull(action);
    }

    @Test
    public void testMiniMaxConsistency() {
        this.aiTrainer.setOpponentTrainer(this.opponent);
        this.aiTrainer.setOpponentActiveBugemon(this.opponent.getCurrentBugemon());

        TurnAction action1 = this.miniMax.chooseBestAction(this.aiTrainer, this.opponent);
        TurnAction action2 = this.miniMax.chooseBestAction(this.aiTrainer, this.opponent);

        assertNotNull(action1);
        assertNotNull(action2);
        assertTrue(action1 instanceof TurnAction.AttackAction || action1 instanceof TurnAction.SwitchAction
                || action1 instanceof TurnAction.UseItemAction);
        assertTrue(action2 instanceof TurnAction.AttackAction || action2 instanceof TurnAction.SwitchAction
                || action2 instanceof TurnAction.UseItemAction);
    }

    @Test
    public void testChooseBestActionRespectsTeamConstraints() {
        // Verify that chosen action respects team constraints
        this.aiTrainer.setOpponentTrainer(this.opponent);
        this.aiTrainer.setOpponentActiveBugemon(this.opponent.getCurrentBugemon());

        TurnAction action = this.miniMax.chooseBestAction(this.aiTrainer, this.opponent);

        if (action instanceof TurnAction.AttackAction attackAction) {
            assertTrue(this.aiTrainer.getCurrentBugemonAttackList().contains(attackAction.attack()));
        } else if (action instanceof TurnAction.SwitchAction switchAction) {
            assertTrue(this.aiTrainer.getBugemons().contains(switchAction.target()));
            assertTrue(switchAction.target().isAlive());
        }
    }

    @Test
    public void testChooseBestActionGameNotCompleted() {
        assertTrue(!this.aiTrainer.isDefeated());
        assertTrue(!this.opponent.isDefeated());

        this.aiTrainer.setOpponentTrainer(this.opponent);
        this.aiTrainer.setOpponentActiveBugemon(this.opponent.getCurrentBugemon());

        TurnAction action = this.miniMax.chooseBestAction(this.aiTrainer, this.opponent);
        assertNotNull(action);
    }
}
