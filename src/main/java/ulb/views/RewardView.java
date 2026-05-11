package ulb.views;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import ulb.Configuration;
import ulb.controllers.RewardController;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.views.components.BugemonTeamView;

public class RewardView extends View {

    private static final Logger LOG = LoggerFactory.getLogger(RewardView.class);

    private Listener listener;

    @FXML
    private Label rewardLabel;
    @FXML
    private VBox rewardsContainer;
    @FXML
    private Button choiceStatButton;
    @FXML
    private Button choiceAttackButton;
    @FXML
    private Button choiceItemButton;
    @FXML
    private VBox teamContainer;
    @FXML
    private BugemonTeamView bugemonsTeamView;
    @FXML
    private HBox bugemonSelectionContainer;
    @FXML
    private VBox attackSelectionContainer;
    @FXML
    private ListView<String> bugemonAttackListView;

    @FXML
    private void onChoiceStatClicked() {
        this.listener.onRewardChosen(0);
    }

    @FXML
    private void onChoiceAttackClicked() {
        this.listener.onRewardChosen(1);
    }

    @FXML
    private void onChoiceItemClicked() {
        this.listener.onRewardChosen(2);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
        this.bugemonsTeamView.setListener(this.listener::onBugemonClicked);
    }

    public void setRewardLabels(String descriptionChoice0, String descriptionChoice1, String descriptionChoice2) {
        this.choiceStatButton.setText(descriptionChoice0);
        this.choiceAttackButton.setText(descriptionChoice1);
        this.choiceItemButton.setText(descriptionChoice2);

        this.rewardLabel.setText("Choisissez votre récompense :");

        this.bugemonSelectionContainer.setVisible(false);
        this.bugemonSelectionContainer.setManaged(false);

        this.rewardsContainer.setVisible(true);
        this.rewardsContainer.setManaged(true);

        this.teamContainer.setVisible(false);
        this.teamContainer.setManaged(false);

        this.attackSelectionContainer.setVisible(false);
        this.attackSelectionContainer.setManaged(false);
    }

    public void showStatSelection(BugemonTeam team) {
        LOG.info("Showing stat selection for team: " + team);
        this.rewardsContainer.setVisible(false);
        this.rewardsContainer.setManaged(false);

        this.bugemonSelectionContainer.setVisible(true);
        this.bugemonSelectionContainer.setManaged(true);

        this.teamContainer.setVisible(true);
        this.teamContainer.setManaged(true);

        this.bugemonsTeamView.setVisible(true);
        this.bugemonsTeamView.setManaged(true);

        this.bugemonsTeamView.showTeam(team, false);

        this.attackSelectionContainer.setVisible(false);
        this.attackSelectionContainer.setManaged(false);

        this.rewardLabel.setText("Sur quel Bugemon appliquer le bonus ?");
    }

    public void showAttackSelection(BugemonTeam team) {
        LOG.info("Showing attack selection for team: " + team);
        this.rewardsContainer.setVisible(false);
        this.rewardsContainer.setManaged(false);

        this.bugemonSelectionContainer.setVisible(true);
        this.bugemonSelectionContainer.setManaged(true);

        this.teamContainer.setVisible(true);
        this.teamContainer.setManaged(true);

        this.bugemonsTeamView.setVisible(true);
        this.bugemonsTeamView.setManaged(true);

        this.bugemonsTeamView.showTeam(team, false);

        this.attackSelectionContainer.setVisible(true);
        this.attackSelectionContainer.setManaged(true);

        this.rewardLabel.setText("Sur quel Bugemon appliquer le bonus ?");
    }

    @Override
    protected String getPath() {
        return Configuration.Paths.Fxml.REWARD_VIEW;
    }

    @Override
    public void refresh() {
        //
    }

    public void setAttackList(List<String> attackNames) {
        this.bugemonAttackListView.getItems().setAll(attackNames);
    }

    public interface Listener {
        void onRewardChosen(int optionIdx);

        void onBugemonClicked(Bugemon bugemon);
    }
}
