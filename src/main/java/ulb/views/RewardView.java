package ulb.views;

import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import ulb.Configuration;
import ulb.models.bugemon.Bugemon;

public class RewardView extends View {

    @FXML
    private Label rewardText;
    @FXML
    private Button choice0Button;
    @FXML
    private Button choice1Button;
    @FXML
    private Button choice2Button;
    @FXML
    private HBox teamContainer;
    @FXML
    private HBox rewardsContainer;

    @FXML
    private void onChoice0Clicked() {
        this.listener.onRewardChosen(0);
    }

    @FXML
    private void onChoice1Clicked() {
        this.listener.onRewardChosen(1);
    }

    @FXML
    private void onChoice2Clicked() {
        this.listener.onRewardChosen(2);
    }

    private Listener listener;

    public void setListener(RewardView.Listener listener) {
        this.listener = listener;
    }

    public void setRewardTexts(String descriptionChoice0, String descriptionChoice1, String descriptionChoice2) {
        choice0Button.setText(descriptionChoice0);
        choice1Button.setText(descriptionChoice1);
        choice2Button.setText(descriptionChoice2);

        rewardsContainer.setVisible(true);
        rewardsContainer.setManaged(true);
        teamContainer.setVisible(false);
        teamContainer.setManaged(false);
        rewardText.setText("Choisissez votre récompense :");
    }

    public void showTeamSelection(List<Bugemon> team) {
        rewardsContainer.setVisible(false);
        rewardsContainer.setManaged(false);

        teamContainer.setVisible(true);
        teamContainer.setManaged(true);
        teamContainer.getChildren().clear();

        rewardText.setText("Choisissez le Bugemon qui recevra le bonus :");

        for (Bugemon bugemon : team) {
            Button bugemonButton = new Button(bugemon.getName());
            bugemonButton.setStyle("-fx-font-size: 14px; -fx-padding: 10px;");

            bugemonButton.setOnAction(event -> {
                if (this.listener != null) {
                    this.listener.onBugemonChosen(bugemon);
                }
            });

            teamContainer.getChildren().add(bugemonButton);
        }
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
