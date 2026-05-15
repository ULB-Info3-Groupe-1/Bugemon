package ulb.models.combat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.models.bugemon.Attack;
import ulb.models.combat.turn.ActionCallback;
import ulb.models.combat.turn.TurnAction;
import ulb.models.combat.turn.TurnAction.AttackAction;
import ulb.models.combat.turn.TurnAction.ForfeitAction;
import ulb.models.combat.turn.TurnAction.ItemAction;
import ulb.models.combat.turn.TurnAction.SwitchAction;
import ulb.models.combat.turn.TurnActionVisitor;
import ulb.models.combat.turn.TurnActionsReadyCallback;
import ulb.models.combat.turn.TurnPhase;
import ulb.models.combat.turn.TurnResolvedCallback;
import ulb.models.combat.turn.TurnStep;
import ulb.models.combat.turn.TurnStep.AttackStep;
import ulb.models.combat.turn.TurnStep.ItemStep;
import ulb.models.combat.turn.TurnStep.KoStep;
import ulb.models.combat.turn.TurnStep.SwitchStep;
import ulb.models.combat.utils.CombatContext;
import ulb.models.combat.utils.DamageCalculator;

public class Combat {
    private static final Logger LOG = LoggerFactory.getLogger(Combat.class);

    private final CombatTeam playerTeam;
    private final CombatTeam opponentTeam;

    private final CombatStrategy playerStrategy;
    private final CombatStrategy opponentStrategy;

    private final DamageCalculator damageCalculator;

    private CombatResult result;
    private boolean finished;

    public Combat(CombatTeam playerTeam, CombatTeam opponentTeam, CombatStrategy playerStrategy,
            CombatStrategy opponentStrategy, DamageCalculator damageCalculator) {
        this.playerTeam = playerTeam;
        this.opponentTeam = opponentTeam;

        this.playerStrategy = playerStrategy;
        this.opponentStrategy = opponentStrategy;

        this.damageCalculator = damageCalculator;

        this.result = null;
        this.finished = false;
    }

    private CombatContext makePlayerContext() {
        return new CombatContext(this.playerTeam, this.opponentTeam);
    }

    private CombatContext makeOpponentContext() {
        return new CombatContext(this.opponentTeam, this.playerTeam);
    }

    public void requestActions(TurnActionsReadyCallback callback) {
        // magic stuff to call onBothActionsReady only once both actions are ready

        // Single element arrays because of an odd java rule with local variables and
        // enclosing scopes
        TurnAction[] playerAction = new TurnAction[1];
        TurnAction[] opponentAction = new TurnAction[1];

        ActionCallback playerCb = action -> {
            playerAction[0] = action;
            if (opponentAction[0] != null) {
                callback.onBothActionsReady(playerAction[0], opponentAction[0]);
            }
        };
        ActionCallback opponentCb = action -> {
            opponentAction[0] = action;
            if (playerAction[0] != null) {
                callback.onBothActionsReady(playerAction[0], opponentAction[0]);
            }
        };

        LOG.debug("Requesting actions from both strategies");
        this.playerStrategy.chooseAction(this.makePlayerContext(), playerCb);
        this.opponentStrategy.chooseAction(this.makeOpponentContext(), opponentCb);
    }

    /**
     * Requests a switch to the given team.
     *
     * This is tipically used when the active bugemon of the team is ko.
     */
    public void requestForcedSwitch(CombatTeam team, ActionCallback callback) {
        boolean isPlayer = team == this.playerTeam;

        CombatStrategy strategy = isPlayer ? this.playerStrategy : this.opponentStrategy;

        CombatContext ctx = isPlayer ? this.makePlayerContext() : this.makeOpponentContext();

        strategy.chooseSwitch(ctx, callback);
    }

    public void resolveTurn(TurnAction playerAction, TurnAction opponentAction, TurnResolvedCallback callback) {
        List<TurnStep> steps = new ArrayList<>();

        List<TurnAction> actions = this.computeActionOrder(playerAction, opponentAction);

        // use == to avoid edge case in which both player and opponent choose the same
        // action.
        boolean firstIsPlayer = actions.get(0) == playerAction;
        boolean secondIsPlayer = !firstIsPlayer;

        // TODO: fix code dup with second team/actor
        // retrieve the bugemon corresponding to the second action
        // to later check if it died from the first action.
        CombatTeam secondTeam = firstIsPlayer ? this.opponentTeam : this.playerTeam;
        CombatBugemon secondActorBefore = secondTeam.getActive();

        LOG.debug("Resolving turn: first={}", firstIsPlayer ? "player" : "opponent");

        // resolve first action
        steps.addAll(this.resolveAction(actions.get(0), firstIsPlayer));

        // if combat .
        if (this.isFinished()) {
            LOG.info("Forfeit detected - combat ends in defeat");
            callback.onTurnResolved(steps);
            return;
        }

        // handle potenatial combat end
        if (this.checkCombatFinished()) {
            LOG.info("Combat ended after first action: result={}", this.result);
            callback.onTurnResolved(steps);
            return;
        }

        // handle potential Ko
        if (secondActorBefore.isKo()) {
            LOG.debug("Second actor KO - requesting forced switch for {}", secondIsPlayer ? "player" : "opponent");
            this.handleKo(steps, callback, secondIsPlayer);
            return;
        }

        // resolve second action
        steps.addAll(this.resolveAction(actions.get(1), secondIsPlayer));

        // handle potenatial combat end
        if (this.checkCombatFinished()) {
            LOG.info("Combat ended after second action: result={}", this.result);
            callback.onTurnResolved(steps);
            return;
        }

        CombatTeam firstTeam = firstIsPlayer ? this.playerTeam : this.opponentTeam;
        CombatBugemon firstActor = firstTeam.getActive();

        if (firstActor.isKo()) {
            LOG.debug("First actor KO - requesting forced switch for {}", firstIsPlayer ? "player" : "opponent");
            this.handleKo(steps, callback, firstIsPlayer);
            return;
        }

        LOG.debug("Turn resolved normally with {} steps", steps.size());
        callback.onTurnResolved(steps);
    }

    private void handleKo(List<TurnStep> turnSteps, TurnResolvedCallback callback, boolean isPlayer) {
        CombatTeam koTeam = isPlayer ? this.playerTeam : this.opponentTeam;

        this.requestForcedSwitch(koTeam, switchAction -> {
            this.applyForcedSwitch(koTeam, switchAction, turnSteps);
            callback.onTurnResolved(turnSteps);
        });
    }

    private void applyForcedSwitch(CombatTeam team, TurnAction action, List<TurnStep> turnSteps) {
        boolean isPlayer = this.playerTeam == team;
        turnSteps.addAll(this.resolveAction(action, isPlayer));
    }

    private List<TurnStep> resolveAction(TurnAction action, boolean isPlayer) {
        CombatTeam actingTeam = isPlayer ? this.playerTeam : this.opponentTeam;
        CombatTeam opposingTeam = isPlayer ? this.opponentTeam : this.playerTeam;
        CombatBugemon actor = actingTeam.getActive();

        if (actor.isKo()) {
            return List.of();
        }

        return action.accept(new TurnActionVisitor() {
            // TODO: handle each case
            public List<TurnStep> visit(AttackAction attackAction) {
                return Combat.this.resolveAttack(actor, opposingTeam.getActive(), attackAction.attack());
            }

            public List<TurnStep> visit(SwitchAction switchAction) {
                actingTeam.setActive(switchAction.target());
                return List.of(new SwitchStep(switchAction.target()));
            }

            public List<TurnStep> visit(ItemAction a) {
                return List.of(new ItemStep());
            }

            public List<TurnStep> visit(ForfeitAction a) {
                Combat.this.result = CombatResult.DEFEAT;
                Combat.this.finished = true;
                return List.of();
            }
        });
    }

    private List<TurnStep> resolveAttack(CombatBugemon attacker, CombatBugemon defender, Attack attack) {
        List<TurnStep> steps = new ArrayList<>();

        int damage = this.damageCalculator.calculateDamage(attacker, defender, attack);

        defender.takeDamage(damage);
        LOG.debug("{} uses {} on {} for {} damage (HP left: {})", attacker, attack.name(), defender, damage,
                defender.getCurrentHp());
        steps.add(new AttackStep(attacker, defender, attack, damage, defender.getCurrentHp()));

        if (defender.isKo()) {
            LOG.info("{} is KO", defender);
            steps.add(new KoStep(defender));
        }

        return steps;
    }

    private List<TurnAction> computeActionOrder(TurnAction playerAction, TurnAction opponentAction) {
        // two attacks -> order by prio
        if (playerAction.phase().equals(TurnPhase.ATTACK) && opponentAction.phase().equals(TurnPhase.ATTACK)) {
            int playerInitiative = this.playerTeam.getActive().getEffectiveInitiative();
            int opponentInitiative = this.opponentTeam.getActive().getEffectiveInitiative();

            return (playerInitiative >= opponentInitiative) ? List.of(playerAction, opponentAction)
                    : List.of(opponentAction, playerAction);
        } else /* order by phases priority */ {
            List<TurnAction> actions = new ArrayList<>();
            actions.add(playerAction);
            actions.add(opponentAction);
            Collections.sort(actions);
            return actions;
        }
    }

    private boolean checkCombatFinished() {
        if (this.playerTeam.isDefeated()) {
            this.result = CombatResult.DEFEAT;
            this.finished = true;
            return true;
        }
        if (this.opponentTeam.isDefeated()) {
            this.result = CombatResult.VICTORY;
            this.finished = true;
            return true;
        }

        return false;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public CombatResult getResult() {
        if (!this.finished) {
            throw new IllegalStateException("Combat not finished yet");
        }
        return this.result;
    }
}
