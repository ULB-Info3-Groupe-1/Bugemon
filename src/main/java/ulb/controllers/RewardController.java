package ulb.controllers;

import java.util.List;

import ulb.common.dto.display.RunBugemonDisplayDTO;
import ulb.models.bugemon.Attack;
import ulb.models.item.Inventory;
import ulb.models.run.RunBugemon;
import ulb.models.run.RunTeam;
import ulb.models.tower.reward.AttackReward;
import ulb.models.tower.reward.BonusStatsReward;
import ulb.models.tower.reward.ItemReward;
import ulb.models.tower.reward.Reward;
import ulb.services.RewardService;
import ulb.views.RewardView;
import ulb.views.ViewLoader;

/**
 * Controller for the post-room reward screen.
 *
 * <p>
 * Presents the player with a choice of rewards after clearing a tower room. Handles three reward types:
 * <ul>
 * <li>{@link ulb.models.tower.reward.ItemReward} — applied immediately to the inventory</li>
 * <li>{@link ulb.models.tower.reward.BonusStatsReward} — applied to a player-selected Bugemon</li>
 * <li>{@link ulb.models.tower.reward.AttackReward} — replaces a selected attack on a player-selected Bugemon</li>
 * </ul>
 */
public class RewardController extends Controller<RewardView> implements RewardView.Listener {

    private final RewardService rewardService;

    private RunTeam runTeam;
    private Inventory inventory;

    private Reward selectedReward;
    private RunBugemon selectedBugemon;

    public RewardController(MetaController metaController, RewardService rewardService) {
        super(metaController, ViewLoader.load(RewardView::new));
        this.rewardService = rewardService;
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
            this.rewardService.applyItemReward(itemReward, this.inventory);
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
            this.rewardService.applyStatBonusReward(statReward, this.selectedBugemon);
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
        this.rewardService.applyAttackReward((AttackReward) this.selectedReward, this.selectedBugemon, attack);
        this.metaController.onRewardFlowFinished();
    }
}
