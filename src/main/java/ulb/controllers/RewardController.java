package ulb.controllers;

import java.util.List;

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
            this.view.showFeedback("Récompense choisie : " + itemReward.getItem().name());
            this.metaController.onRewardFlowFinished();
        } else {
            String prompt = reward instanceof AttackReward
                    ? "Quel Bugémon apprend cette attaque ?"
                    : "Quel Bugémon reçoit ce bonus de statistiques ?";
            List<String> labels = this.runTeam.getMembers().stream()
                    .map(b -> b.getName() + "  Nv." + b.getLevel()
                            + "  HP:" + b.getCurrentHp() + "/" + b.getMaxHp()
                            + "  [" + b.getType() + "]")
                    .toList();
            this.view.displayTeamForSelection(labels, prompt);
        }
    }

    @Override
    public void onBugemonSelected(int index) {
        this.selectedBugemon = this.runTeam.getMembers().get(index);
        if (this.selectedReward instanceof BonusStatsReward statReward) {
            this.rewardService.applyStatBonusReward(statReward, this.selectedBugemon);
            this.metaController.onRewardFlowFinished();
        } else if (this.selectedReward instanceof AttackReward attackReward) {
            String title = "Quelle attaque de " + this.selectedBugemon.getName()
                    + " remplacer par " + attackReward.getAttack().name() + " ?";
            this.view.showAttackReplacement(title, this.selectedBugemon.getAttacks());
        }
    }

    @Override
    public void onAttackChosen(Attack attack) {
        this.rewardService.applyAttackReward((AttackReward) this.selectedReward, this.selectedBugemon, attack);
        this.metaController.onRewardFlowFinished();
    }
}
