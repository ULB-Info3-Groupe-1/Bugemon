package ulb.views.components;

import java.util.List;
import java.util.Set;
import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;

import ulb.Configuration;
import ulb.models.player.PlayerBugemon;

/** Reusable custom component displaying all the bugemons inside of a grid. */
public class AllBugemonsView extends ComponentView {

    @FXML
    private FlowPane flowPane;

    private Listener listener;

    public AllBugemonsView() {
        super(Configuration.Paths.Fxml.COMPONENT_ALL_BUGEMONS);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    /** Clears and repopulates the grid with the given list of Bugemons. */
    public void showAll(List<PlayerBugemon> bugemonList, Set<PlayerBugemon> selectedBugemons) {
        this.flowPane.getChildren().clear();

        for (PlayerBugemon bugemon : bugemonList) {
            BugemonCardView bugemonCard = this.createBugemonCard(bugemon);

            if (selectedBugemons.contains(bugemon)) {
                bugemonCard.select();
            } else {
                bugemonCard.unselect();
            }

            this.flowPane.getChildren().add(bugemonCard);
        }
    }

    private BugemonCardView createBugemonCard(PlayerBugemon bugemon) {
        BugemonCardView card = new BugemonCardView(bugemon);
        card.hideLevelLabel();

        card.setListener(this.listener::onBugemonClicked);

        return card;
    }

    public interface Listener {

        void onBugemonClicked(PlayerBugemon bugemon);

    }
}
