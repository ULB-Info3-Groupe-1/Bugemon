package ulb.controllers;

import java.util.List;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.Inventory;
import ulb.models.bugemon.Item;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.reward.Reward;
import ulb.services.BugemonService;
import ulb.services.RewardService;
import ulb.services.TeamService;
import ulb.views.RewardView;
import ulb.views.ViewLoader;

public class RewardController extends Controller<RewardView> implements RewardView.Listener {
    private RewardService rewardService = new RewardService();
    private TeamService teamService;
    private BugemonService bugemonService;
    private Reward pendingReward;

    public RewardController(MetaController metaController, RewardService rewardService,
                            TeamService teamService, BugemonService bugemonService) {
        super(metaController, ViewLoader.load(RewardView::new));
        this.rewardService = rewardService;
        this.teamService = teamService;
        this.bugemonService = bugemonService;

        this.view.setListener(this);
    }

    public void startRewardPhase() {
        this.rewardService.generateRewards();
        this.updateDisplay();
    }

    @Override
    public void onRewardChosen(int optionId) {
        Reward selectedReward = this.rewardService.generateRewards().get(optionId);
        BugemonTeam activeBugemon = teamService.getWorkingTeam();
        List<Bugemon> bugemons = activeBugemon.getAll();
        switch (selectedReward.getRewardType()) {
            case ITEM -> {
                selectedReward.applyReward(null);
                if (metaController.isTowerActive()) {
                    this.metaController.onTower();
                } else {
                    this.metaController.onMainMenu();
                }
            }
            case STAT, ATTACK -> {
                this.pendingReward = selectedReward;
                this.view.showTeamSelection(bugemons);
            }
        }
    }

    @Override
    public void onBugemonChosen(Bugemon bugemon) {
        if (this.pendingReward != null) {
            this.pendingReward.applyReward(bugemon);
            this.bugemonService.saveBugemonState(bugemon);
            if (metaController.isTowerActive()) {
                this.metaController.onTower();
            } else {
                this.metaController.onMainMenu();
            }
        }
    }

    public void updateDisplay() {
        if (this.rewardService == null)
            return; // Sécurité

        List<Reward> options = this.rewardService.generateRewards();
        this.view.setRewardTexts(options.get(0).getSummary(), options.get(1).getSummary(), options.get(2).getSummary());
    }
}
