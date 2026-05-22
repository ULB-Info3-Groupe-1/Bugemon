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

public class RewardController extends Controller<RewardView> implements RewardView.Listener {

    private final RewardService rewardService;

    private List<Reward> rewards;
    private RunTeam runTeam;
    private Inventory inventory;

    private Reward selectedReward;
    private RunBugemon selectedBugemon;

    public RewardController(MetaController metaController, RewardService rewardService) {
        super(metaController, ViewLoader.load(RewardView::new));
        this.rewardService = rewardService;
        this.view.setListener(this);
    }

    public void initialize(List<Reward> newRewards, RunTeam newRunTeam, Inventory newInventory) {
        this.rewards = newRewards;
        this.runTeam = newRunTeam;
        this.inventory = newInventory;
        this.selectedReward = null;
        this.selectedBugemon = null;
        this.view.displayRewardOptions(this.rewards);
    }

    @Override
    public void onRewardChosen(Reward reward) {
        this.selectedReward = reward;
        if (reward instanceof ItemReward itemReward) {
            this.rewardService.applyItemReward(itemReward, this.inventory);
            this.view.showItemRewardApplied(itemReward);
            this.metaController.onRewardFlowFinished();
        } else {
            List<RunBugemonDisplayDTO> dtos = this.runTeam.getMembers().stream()
                    .map(b -> new RunBugemonDisplayDTO(b.getName(), b.getLevel(), b.getCurrentHp(), b.getMaxHp(),
                            b.getType()))
                    .toList();
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
