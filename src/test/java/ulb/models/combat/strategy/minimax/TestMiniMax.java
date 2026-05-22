package ulb.models.combat.strategy.minimax;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.ElementType;
import ulb.models.combat.snapshot.CombatBugemonSnapshot;
import ulb.models.combat.snapshot.CombatSnapshot;
import ulb.models.combat.snapshot.TeamSnapshot;
import ulb.models.item.Item;

public class TestMiniMax {
    private static final Attack BASIC_ATTACK = new Attack("atk-1", "Basic", "Basic attack", 20, ElementType.FLORA,
            List.of());

    private MiniMax miniMax(int depth) {
        return new MiniMax(depth);
    }

    private CombatSnapshot createAttackOnlySnapshot() {
        CombatBugemonSnapshot aiActive = new CombatBugemonSnapshot(40, List.of(BASIC_ATTACK), 40, 10, 10, 5);
        CombatBugemonSnapshot opponentActive = new CombatBugemonSnapshot(40, List.of(BASIC_ATTACK), 40, 10, 10, 4);

        TeamSnapshot opponentTeam = new TeamSnapshot(List.of(opponentActive), opponentActive);
        TeamSnapshot aiTeam = new TeamSnapshot(List.of(aiActive), aiActive);

        return new CombatSnapshot(opponentTeam, aiTeam, Map.of(), Map.of());
    }

    private CombatSnapshot createForcedSwitchSnapshot() {
        CombatBugemonSnapshot aiKO = new CombatBugemonSnapshot(0, List.of(BASIC_ATTACK), 40, 10, 10, 5);
        CombatBugemonSnapshot aiReserve = new CombatBugemonSnapshot(30, List.of(BASIC_ATTACK), 30, 10, 10, 4);
        CombatBugemonSnapshot opponentActive = new CombatBugemonSnapshot(40, List.of(BASIC_ATTACK), 40, 10, 10, 4);

        TeamSnapshot opponentTeam = new TeamSnapshot(List.of(opponentActive), opponentActive);
        TeamSnapshot aiTeam = new TeamSnapshot(List.of(aiKO, aiReserve), aiKO);

        return new CombatSnapshot(opponentTeam, aiTeam, Map.of(), Map.of());
    }

    @Test
    public void chooseBestActionReturnsAttackForAiTeam() {
        SimAction action = this.miniMax(2).chooseBestAction(this.createAttackOnlySnapshot(), true);

        assertNotNull(action);
        assertEquals(SimActionKind.ATTACK, action.kind());
        assertEquals(0, action.index());
    }

    @Test
    public void chooseBestActionReturnsSwitchWhenAiActiveIsKo() {
        SimAction action = this.miniMax(2).chooseBestAction(this.createForcedSwitchSnapshot(), true);

        assertNotNull(action);
        assertEquals(SimActionKind.SWITCH, action.kind());
        assertTrue(action.index() >= 0);
    }

    @Test
    public void chooseBestActionReturnsNoneWhenNoActionExists() {
        CombatBugemonSnapshot aiActive = new CombatBugemonSnapshot(0, List.of(), 40, 10, 10, 5);
        CombatBugemonSnapshot opponentActive = new CombatBugemonSnapshot(0, List.of(), 40, 10, 10, 4);
        TeamSnapshot opponentTeam = new TeamSnapshot(List.of(opponentActive), opponentActive);
        TeamSnapshot aiTeam = new TeamSnapshot(List.of(aiActive), aiActive);
        CombatSnapshot snapshot = new CombatSnapshot(opponentTeam, aiTeam, Map.<Item, Integer>of(),
                Map.<Item, Integer>of());

        SimAction action = this.miniMax(2).chooseBestAction(snapshot, true);

        assertNotNull(action);
        assertEquals(SimActionKind.NONE, action.kind());
    }
}
