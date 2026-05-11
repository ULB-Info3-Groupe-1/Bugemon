package ulb.views;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import ulb.Configuration;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.views.components.BugemonTeamView;

public class RewardView extends View {

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
        this.bugemonsTeamView.setListener(this.listener::onBugemonChosen);
    }

    public void setRewardLabels(String descriptionChoice0, String descriptionChoice1, String descriptionChoice2) {
        this.choiceStatButton.setText(descriptionChoice0);
        this.choiceAttackButton.setText(descriptionChoice1);
        this.choiceItemButton.setText(descriptionChoice2);

        this.rewardLabel.setText("Choisissez votre récompense :");

        this.rewardsContainer.setVisible(true);
        this.rewardsContainer.setManaged(true);

        this.teamContainer.setVisible(false);
        this.teamContainer.setManaged(false);
    }

    public void showTeamSelection(BugemonTeam team) {
        // 1. On cache les boutons de récompenses
        this.rewardsContainer.setVisible(false);
        this.rewardsContainer.setManaged(false);

        // 2. LA CLÉ EST ICI : On affiche la VBox parente !
        this.teamContainer.setVisible(true);
        this.teamContainer.setManaged(true);

        // (Optionnel) Tu peux t'assurer que la vue interne est aussi visible
        this.bugemonsTeamView.setVisible(true);
        this.bugemonsTeamView.setManaged(true);

        // 3. On donne les données à afficher
        this.bugemonsTeamView.showTeam(team, false);

        // 4. On met à jour le titre
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

    public interface Listener {
        void onRewardChosen(int optionIdx);

        void onBugemonChosen(Bugemon bugemon);
    }
}
