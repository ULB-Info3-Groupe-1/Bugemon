package ulb.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.models.reward.AttackReward;
import ulb.models.reward.Reward;
import ulb.services.BugemonService;
import ulb.services.RewardService;
import ulb.services.TeamService;
import ulb.views.RewardView;
import ulb.views.ViewLoader;
import ulb.views.components.BugemonTeamView;

public class RewardController extends Controller<RewardView> implements RewardView.Listener, BugemonTeamView.Listener {
    private static final Logger LOG = LoggerFactory.getLogger(RewardController.class);

    private RewardService rewardService;
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
        List<Reward> currentOptions = this.rewardService.getCurrentRewardOptions();
        Reward selectedReward = currentOptions.get(optionId);
        BugemonTeam activeBugemonTeam = this.teamService.getRequiredActiveTeam();
        if (activeBugemonTeam == null) {
            return;
        }

        switch (selectedReward.getRewardType()) {
            case ITEM -> {
                selectedReward.applyReward(null);
                this.quitRewardScreen();
            }
            case STAT -> {
                this.pendingReward = selectedReward;
                this.view.showStatSelection(activeBugemonTeam);
            }
            case ATTACK -> {
                this.pendingReward = selectedReward;
                this.view.showAttackSelection(activeBugemonTeam);
            }
            default -> {
            }
        }
    }

    @Override
    public void onBugemonClicked(Bugemon bugemon) {
        LOG.info("Applying reward to " + bugemon.getName());
        if (this.pendingReward == null) {
            throw new IllegalStateException("Impossible to assign a null reward to" + bugemon.getName());
        }

        // TODO (just to be found)
        // disclaimer: WE MUST USE THE SWITCH CASE IN ORDER TO UPDATE THE VIEW PROPERLY
        // hihi stinky poopy

        switch (this.pendingReward.getRewardType()) {
            case ATTACK -> {
                this.updateAttackSelection(bugemon);
                break;
            }
            default -> {
                this.pendingReward.applyReward(bugemon);
                this.bugemonService.saveBugemonState(bugemon);
                this.quitRewardScreen();
                break;
            }
        }
    }

    @Override
    public void onAttackChosen(Bugemon bugemon, int attackIndex) {

        AttackReward attackReward = (AttackReward) this.pendingReward;
        attackReward.setIndexToReplace(attackIndex);
        attackReward.applyReward(bugemon);
        this.bugemonService.saveBugemonState(bugemon);
        this.quitRewardScreen();
    }

    private void updateAttackSelection(Bugemon bugemon) {
        List<String> updatedAttacks = bugemon.getAttackList().stream().map(Attack::name).toList();
        this.view.setAttackList(updatedAttacks);
    }

    private void quitRewardScreen() {
        LOG.info("Leaving reward room");
        if (this.metaController.isTowerActive()) {
            this.metaController.onRewardChoiceFinished();
        } else {
            throw new IllegalStateException("Tower is not active");
        }
    }

    public void updateDisplay() {
        if (this.rewardService == null) {
            throw new IllegalStateException("No reward service");
        }
        List<Reward> options = this.rewardService.getCurrentRewardOptions();
        this.view.setRewardLabels(options.get(0).getSummary(), options.get(1).getSummary(),
                options.get(2).getSummary());
    }
}
