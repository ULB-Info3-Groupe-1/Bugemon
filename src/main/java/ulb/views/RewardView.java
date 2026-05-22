package ulb.views;

import java.util.List;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import ulb.Configuration;
import ulb.common.dto.display.RunBugemonDisplayDTO;
import ulb.models.bugemon.Attack;
import ulb.models.tower.reward.AttackReward;
import ulb.models.tower.reward.BonusStatsReward;
import ulb.models.tower.reward.ItemReward;
import ulb.models.tower.reward.Reward;

public class RewardView extends View {

    @FXML
    private VBox rewardPanel;
    @FXML
    private HBox rewardCardsBox;
    @FXML
    private Label feedbackLabel;

    @FXML
    private VBox selectionPanel;
    @FXML
    private Label promptLabel;
    @FXML
    private VBox bugemonListBox;
    @FXML
    private VBox attackReplacementBox;
    @FXML
    private Label attackReplacementTitle;
    @FXML
    private HBox attackButtonsBox;

    private Listener listener;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void displayRewardOptions(List<Reward> options) {
        this.rewardCardsBox.getChildren().clear();
        this.feedbackLabel.setText("");
        String[] colors = {"#7a5c00", "#1a4a7a", "#2d6a4f"};
        for (int i = 0; i < options.size(); i++) {
            VBox card = this.createRewardCard(options.get(i), colors[i % colors.length]);
            this.rewardCardsBox.getChildren().add(card);
        }
        this.setPanel(Panel.REWARD);
    }

    public void displayTeamForSelection(List<RunBugemonDisplayDTO> members, Reward reward) {
        this.promptLabel.setText(this.formatTeamSelectionPrompt(reward));
        this.bugemonListBox.getChildren().clear();
        this.bugemonListBox.setVisible(true);
        this.bugemonListBox.setManaged(true);
        this.attackReplacementBox.setVisible(false);
        this.attackReplacementBox.setManaged(false);
        for (int i = 0; i < members.size(); i++) {
            final int index = i;
            Button btn = new Button(this.formatBugemonLabel(members.get(i)));
            btn.setPrefWidth(400);
            btn.setAlignment(Pos.CENTER_LEFT);
            btn.setStyle("-fx-background-color: #2a3a5a; -fx-text-fill: white; "
                    + "-fx-font-size: 13; -fx-background-radius: 8; -fx-padding: 10 16;");
            btn.setOnAction(e -> {
                if (this.listener != null) {
                    this.listener.onBugemonSelected(index);
                }
            });
            this.bugemonListBox.getChildren().add(btn);
        }
        this.setPanel(Panel.SELECTION);
    }

    public void showAttackReplacement(RunBugemonDisplayDTO bugemon, AttackReward attackReward,
            List<Attack> currentAttacks) {
        this.attackReplacementTitle.setText(
                "Quelle attaque de " + bugemon.name() + " remplacer par " + attackReward.getAttack().name() + " ?");
        this.attackButtonsBox.getChildren().clear();
        for (Attack attack : currentAttacks) {
            Button btn = new Button(attack.name() + "\n(" + attack.type() + ", Puissance : " + attack.power() + ")");
            btn.setPrefSize(150, 70);
            btn.setWrapText(true);
            btn.setStyle("-fx-background-color: #5a3a7a; -fx-text-fill: white; "
                    + "-fx-font-size: 11; -fx-background-radius: 8;");
            btn.setOnAction(e -> {
                if (this.listener != null) {
                    this.listener.onAttackChosen(attack);
                }
            });
            this.attackButtonsBox.getChildren().add(btn);
        }
        this.bugemonListBox.setVisible(false);
        this.bugemonListBox.setManaged(false);
        this.attackReplacementBox.setVisible(true);
        this.attackReplacementBox.setManaged(true);
    }

    public void showItemRewardApplied(ItemReward itemReward) {
        this.feedbackLabel.setText("Récompense choisie : " + itemReward.getItem().name());
    }

    private String formatTeamSelectionPrompt(Reward reward) {
        if (reward instanceof AttackReward) {
            return "Quel Bugémon apprend cette attaque ?";
        }
        return "Quel Bugémon reçoit ce bonus de statistiques ?";
    }

    private String formatBugemonLabel(RunBugemonDisplayDTO bugemon) {
        return bugemon.name() + "  Nv." + bugemon.level() + "  HP:" + bugemon.currentHp() + "/" + bugemon.maxHp()
                + "  [" + bugemon.type() + "]";
    }

    private void setPanel(Panel panel) {
        this.rewardPanel.setVisible(panel == Panel.REWARD);
        this.rewardPanel.setManaged(panel == Panel.REWARD);
        this.selectionPanel.setVisible(panel == Panel.SELECTION);
        this.selectionPanel.setManaged(panel == Panel.SELECTION);
    }

    private VBox createRewardCard(Reward reward, String bgColor) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(200, 220);
        card.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 10; -fx-padding: 14;");

        Label typeLabel = new Label(this.formatRewardType(reward));
        typeLabel.setStyle("-fx-text-fill: #ffd700; -fx-font-size: 11; -fx-font-weight: bold;");
        typeLabel.setWrapText(true);

        Label nameLabel = new Label(this.formatRewardName(reward));
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold;");
        nameLabel.setWrapText(true);

        Label descLabel = new Label(this.formatRewardDescription(reward));
        descLabel.setStyle("-fx-text-fill: #ccc; -fx-font-size: 11;");
        descLabel.setWrapText(true);

        Button btn = new Button("Choisir");
        btn.setStyle("-fx-background-color: #ffd700; -fx-text-fill: #1a1a2e; "
                + "-fx-font-weight: bold; -fx-background-radius: 6;");
        btn.setOnAction(e -> {
            if (this.listener != null) {
                this.listener.onRewardChosen(reward);
            }
        });

        card.getChildren().addAll(typeLabel, nameLabel, descLabel, btn);
        return card;
    }

    private String formatRewardType(Reward reward) {
        if (reward instanceof ItemReward) {
            return "OBJET";
        }
        if (reward instanceof AttackReward) {
            return "ATTAQUE";
        }
        if (reward instanceof BonusStatsReward) {
            return "BONUS STATS";
        }
        return "RÉCOMPENSE";
    }

    private String formatRewardName(Reward reward) {
        if (reward instanceof ItemReward r) {
            return r.getItem().name();
        }
        if (reward instanceof AttackReward r) {
            return r.getAttack().name();
        }
        if (reward instanceof BonusStatsReward r) {
            var b = r.getBonus();
            StringBuilder sb = new StringBuilder();
            if (b.getBonusHp() != 0) {
                sb.append("+").append(b.getBonusHp()).append(" HP ");
            }
            if (b.getBonusAttack() != 0) {
                sb.append("+").append(b.getBonusAttack()).append(" Atk ");
            }
            if (b.getBonusDefense() != 0) {
                sb.append("+").append(b.getBonusDefense()).append(" Déf ");
            }
            if (b.getBonusInitiative() != 0) {
                sb.append("+").append(b.getBonusInitiative()).append(" Init");
            }
            return sb.toString().trim();
        }
        return "";
    }

    private String formatRewardDescription(Reward reward) {
        if (reward instanceof ItemReward r) {
            return r.getItem().description();
        }
        if (reward instanceof AttackReward r) {
            return "Type : " + r.getAttack().type() + "\nPuissance : " + r.getAttack().power();
        }
        if (reward instanceof BonusStatsReward) {
            return "Bonus permanent de statistiques";
        }
        return "";
    }

    @Override
    public String getPath() {
        return Configuration.Paths.Fxml.REWARD_VIEW;
    }

    @Override
    public void refresh() {
        // state is managed via displayRewardOptions / displayTeamForSelection / showAttackReplacement
    }

    private enum Panel {
        REWARD,
        SELECTION
    }

    public interface Listener {
        void onRewardChosen(Reward reward);

        void onBugemonSelected(int index);

        void onAttackChosen(Attack attack);
    }
}
