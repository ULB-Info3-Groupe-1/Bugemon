package ulb.controllers;

import java.util.List;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.reward.Reward;
import ulb.services.BugemonService;
import ulb.services.RewardService;
import ulb.services.TeamService;
import ulb.views.RewardView;
import ulb.views.ViewLoader;

public class RewardController extends Controller<RewardView> implements RewardView.Listener {
    private RewardService rewardService;
    private TeamService teamService;
    private BugemonService bugemonService;
    private Reward pendingReward;

    public RewardController(MetaController metaController) {
        super(metaController, ViewLoader.load(RewardView::new));
        this.view.setListener(this);
    }

    public void startRewardPhase() {
        this.rewardService.generateRewards();
        this.updateDisplay();
    }

    @Override
    public void onRewardChosen(int optionId) {
        List<Reward> currentOptions = this.rewardService.getCurrentRewardOptions();
        Reward selectedReward = currentOptions.get(optionId);
        BugemonTeam activeBugemonTeam = this.teamService.getWorkingTeam();
        switch (selectedReward.getRewardType()) {
            case ITEM -> {
                selectedReward.applyReward(null);
                this.quitRewardScreen();
            }
            case STAT, ATTACK -> {
                this.pendingReward = selectedReward;
                this.view.showTeamSelection(activeBugemonTeam);
            }
            default -> {
                //
            }

        }
    }

    @Override
    public void onBugemonChosen(Bugemon bugemon) {
        if (this.pendingReward == null) {
            throw new IllegalStateException("Impossible to assign a null reward to" + bugemon.getName());
        }
        this.pendingReward.applyReward(bugemon);
        this.bugemonService.saveBugemonState(bugemon);
        this.quitRewardScreen();
    }

    private void quitRewardScreen() {
        if (metaController.isTowerActive()) {
            this.metaController.onTower();
        } else {
            throw new IllegalStateException("Tower is not active");
        }
    }

    public void updateDisplay() {
        if (this.rewardService == null) {
            throw new IllegalStateException("No reward service");
        }
        List<Reward> options = this.rewardService.generateRewards();
        this.view.setRewardTexts(options.get(0).getSummary(), options.get(1).getSummary(), options.get(2).getSummary());
    }
}
