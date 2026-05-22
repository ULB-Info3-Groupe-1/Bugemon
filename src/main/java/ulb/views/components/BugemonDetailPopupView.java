package ulb.views.components;

import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import ulb.Configuration;
import ulb.common.dto.display.BugemonDisplayDTO;
import ulb.models.bugemon.Attack;

/**
 * Read-only popup displaying a Bugemon's stats and attacks. Instantiate with the bugemon to display, then call
 * {@link #show(ContextMenuEvent)} to open it centred over the owner window.
 */
public class BugemonDetailPopupView extends ComponentView {

    @FXML
    private ImageView sprite;
    @FXML
    private Label nameLabel;
    @FXML
    private Label typeLabel;
    @FXML
    private Label levelLabel;
    @FXML
    private Label hpValue;
    @FXML
    private Label attackValue;
    @FXML
    private Label defenseValue;
    @FXML
    private Label initiativeValue;
    @FXML
    private VBox attacksContainer;

    private Stage popupStage;

    public BugemonDetailPopupView(BugemonDisplayDTO bugemon) {
        super(Configuration.Paths.Fxml.COMPONENT_BUGEMON_DETAIL_POPUP);
        File spriteFile = new File(Configuration.Paths.SPRITES + bugemon.base().spritePath());
        this.sprite.setImage(new Image(spriteFile.toURI().toString(), 72, 72, true, false));
        this.nameLabel.setText(bugemon.base().name());
        this.nameLabel.getStyleClass().addAll("bugemon-name", bugemon.base().type().toString());
        this.typeLabel.setText(bugemon.base().type().toString());
        this.typeLabel.getStyleClass().add(bugemon.base().type().toString());
        this.levelLabel.setText("Nv. " + bugemon.level());
        this.hpValue.setText(String.valueOf(bugemon.getMaxHp()));
        this.attackValue.setText(String.valueOf(bugemon.getAttack()));
        this.defenseValue.setText(String.valueOf(bugemon.getDefense()));
        this.initiativeValue.setText(String.valueOf(bugemon.getInitiative()));
        for (Attack attack : bugemon.attacks()) {
            Label row = new Label("• " + attack.name() + "  (" + attack.type() + ")  [" + attack.power() + "]");
            row.getStyleClass().add("bugemon-popup-attack");
            this.attacksContainer.getChildren().add(row);
        }
    }

    /**
     * Shows the popup centred over the owner window, closing it when it loses focus.
     */
    public void show(ContextMenuEvent event) {
        Node source = (Node) event.getSource();
        Window owner = source.getScene().getWindow();

        if (this.popupStage == null) {
            this.popupStage = new Stage(StageStyle.TRANSPARENT);
            this.popupStage.initOwner(owner);

            Scene scene = new Scene(this);
            scene.setFill(Color.TRANSPARENT);
            scene.getStylesheets().addAll(source.getScene().getStylesheets());

            this.popupStage.setScene(scene);

            this.popupStage.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
                if (Boolean.FALSE.equals(isFocused)) {
                    this.popupStage.close();
                }
            });
        }

        this.popupStage.show();
        this.popupStage.setX(owner.getX() + (owner.getWidth() - this.popupStage.getWidth()) / 2);
        this.popupStage.setY(owner.getY() + (owner.getHeight() - this.popupStage.getHeight()) / 2);
    }
}
