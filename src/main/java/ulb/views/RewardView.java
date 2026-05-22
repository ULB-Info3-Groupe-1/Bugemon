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
        for (Reward option : options) {
            this.rewardCardsBox.getChildren().add(this.createRewardCard(option));
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
            btn.setAlignment(Pos.CENTER_LEFT);
            btn.getStyleClass().addAll("btn", "btn-action-blue", "reward-bugemon-btn");
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
            btn.setWrapText(true);
            btn.getStyleClass().addAll("btn", "attack-" + attack.type().name(), "reward-attack-btn");
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

    private VBox createRewardCard(Reward reward) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().addAll("reward-card", this.rewardCardClass(reward));

        Label typeLabel = new Label(this.formatRewardType(reward));
        typeLabel.getStyleClass().add("reward-card-type-label");
        typeLabel.setWrapText(true);

        Label nameLabel = new Label(this.formatRewardName(reward));
        nameLabel.getStyleClass().add("reward-card-name");
        nameLabel.setWrapText(true);

        Label descLabel = new Label(this.formatRewardDescription(reward));
        descLabel.getStyleClass().add("reward-card-desc");
        descLabel.setWrapText(true);

        Button btn = new Button("Choisir");
        btn.getStyleClass().add("reward-card-btn");
        btn.setOnAction(e -> {
            if (this.listener != null) {
                this.listener.onRewardChosen(reward);
            }
        });

        card.getChildren().addAll(typeLabel, nameLabel, descLabel, btn);
        return card;
    }

    private String rewardCardClass(Reward reward) {
        if (reward instanceof ItemReward) {
            return "reward-card-item";
        }
        if (reward instanceof AttackReward) {
            return "reward-card-attack";
        }
        return "reward-card-bonus";
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
        return "RECOMPENSE";
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
                sb.append("+").append(b.getBonusDefense()).append(" Def ");
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
