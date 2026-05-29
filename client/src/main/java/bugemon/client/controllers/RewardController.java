package bugemon.client.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bugemon.client.services.RemoteBugemonService;
import bugemon.client.services.RemoteInventoryService;
import bugemon.client.views.RewardView;
import bugemon.client.views.ViewLoader;
import bugemon.common.dto.display.RunBugemonDisplayDTO;
import bugemon.common.models.bugemon.Attack;
import bugemon.common.models.combat.damage.Efficiency;
import bugemon.common.models.item.Inventory;
import bugemon.common.models.player.PlayerBugemon;
import bugemon.common.models.player.exceptions.IllegalAttackReplacementException;
import bugemon.common.models.run.RunBugemon;
import bugemon.common.models.run.RunTeam;
import bugemon.common.models.tower.reward.AttackReward;
import bugemon.common.models.tower.reward.BonusStatsReward;
import bugemon.common.models.tower.reward.ItemReward;
import bugemon.common.models.tower.reward.Reward;

/**
 * Controller for the post-room reward screen.
 *
 * <p>
 * Rewards are applied to the model locally (pure mutations on the inventory or a run Bugemon) and the affected state is
 * then persisted asynchronously through {@link RemoteInventoryService} / {@link RemoteBugemonService}. Three reward
 * types are handled: items (applied immediately), stat bonuses and attack replacements (applied to a player-selected
 * Bugemon).
 */
public class RewardController extends Controller<RewardView> implements RewardView.Listener {

    private static final Logger LOG = LoggerFactory.getLogger(RewardController.class);

    private final RemoteInventoryService inventoryService;
    private final RemoteBugemonService bugemonService;

    private RunTeam runTeam;
    private Inventory inventory;

    private Reward selectedReward;
    private RunBugemon selectedBugemon;

    public RewardController(MetaController metaController, RemoteInventoryService inventoryService,
            RemoteBugemonService bugemonService) {
        super(metaController, ViewLoader.load(RewardView::new));
        this.inventoryService = inventoryService;
        this.bugemonService = bugemonService;
        this.view.setListener(this);
    }

    /**
     * Prepares the reward screen for a new reward selection.
     *
     * @param newRewards
     *            the rewards available for this room
     * @param newRunTeam
     *            the player's current run team, used for Bugemon selection steps
     * @param newInventory
     *            the player's inventory, used when applying item rewards
     */
    public void initialize(List<Reward> newRewards, RunTeam newRunTeam, Inventory newInventory) {
        this.runTeam = newRunTeam;
        this.inventory = newInventory;
        this.selectedReward = null;
        this.selectedBugemon = null;
        this.view.displayRewardOptions(newRewards);
    }

    @Override
    public void onRewardChosen(Reward reward) {
        this.selectedReward = reward;
        if (reward instanceof ItemReward itemReward) {
            this.applyItemReward(itemReward);
            this.view.showItemRewardApplied(itemReward);
            this.metaController.onRewardFlowFinished();
        } else {
            List<RunBugemon> eligible = (reward instanceof AttackReward ar)
                    ? this.runTeam.getEligibleFor(ar.getAttack())
                    : this.runTeam.getMembers();
            List<RunBugemonDisplayDTO> dtos = eligible.stream().map(b -> new RunBugemonDisplayDTO(b.getName(),
                    b.getLevel(), b.getCurrentHp(), b.getMaxHp(), b.getType())).toList();
            this.view.displayTeamForSelection(dtos, reward);
        }
    }

    @Override
    public void onBugemonSelected(int index) {
        this.selectedBugemon = this.runTeam.getMembers().get(index);
        if (this.selectedReward instanceof BonusStatsReward statReward) {
            this.applyStatBonusReward(statReward, this.selectedBugemon);
            this.metaController.onRewardFlowFinished();
        } else if (this.selectedReward instanceof AttackReward attackReward) {
            RunBugemonDisplayDTO dto = new RunBugemonDisplayDTO(this.selectedBugemon.getName(),
                    this.selectedBugemon.getLevel(), this.selectedBugemon.getCurrentHp(),
                    this.selectedBugemon.getMaxHp(), this.selectedBugemon.getType());
            this.view.showAttackReplacement(dto, attackReward, this.selectedBugemon.getAttacks());
        }
    }

    @Override
    public void onAttackChosen(Attack attack) {
        this.applyAttackReward((AttackReward) this.selectedReward, this.selectedBugemon, attack);
        this.metaController.onRewardFlowFinished();
    }

    private void applyItemReward(ItemReward reward) {
        this.inventory.addItem(reward.getItem(), reward.getQuantity());
        this.persistInventory();
    }

    private void applyStatBonusReward(BonusStatsReward reward, RunBugemon bugemon) {
        bugemon.applyBonus(reward.getBonus());
        this.persistBugemon(bugemon.getPlayerBugemon());
    }

    private void applyAttackReward(AttackReward reward, RunBugemon bugemon, Attack toReplace) {
        PlayerBugemon playerBugemon = bugemon.getPlayerBugemon();
        if (Efficiency.preview(reward.getAttack().type(), playerBugemon.getType()) == Efficiency.SUPER_EFFICIENT) {
            return;
        }
        try {
            playerBugemon.replaceAttack(toReplace, reward.getAttack());
            this.persistBugemon(playerBugemon);
        } catch (IllegalAttackReplacementException e) {
            LOG.debug("Attack replacement rejected: {}", e.getMessage());
        }
    }

    private void persistInventory() {
        this.inventoryService.save(this.inventory).whenComplete((ignored, error) -> {
            if (error != null) {
                LOG.warn("Failed to persist inventory reward", error);
            }
        });
    }

    private void persistBugemon(PlayerBugemon playerBugemon) {
        this.bugemonService.savePlayerBugemon(playerBugemon).whenComplete((ignored, error) -> {
            if (error != null) {
                LOG.warn("Failed to persist Bugemon reward", error);
            }
        });
    }
}
