package ulb.views.components;

import java.util.List;
import java.util.Set;
import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;

import ulb.Configuration;
import ulb.common.dto.PlayerBugemonDTO;

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
    public void showAll(List<PlayerBugemonDTO> playerBugemonList, Set<PlayerBugemonDTO> selectedBugemons) {
        this.flowPane.getChildren().clear();

        for (PlayerBugemonDTO playerBugemon : playerBugemonList) {
            BugemonCardView bugemonCard = this.createBugemonCard(playerBugemon);

            if (selectedBugemons.contains(playerBugemon)) {
                bugemonCard.select();
            } else {
                bugemonCard.unselect();
            }

            this.flowPane.getChildren().add(bugemonCard);
        }
    }

    private BugemonCardView createBugemonCard(PlayerBugemonDTO playerBugemon) {
        BugemonCardView card = new BugemonCardView(playerBugemon);
        card.hideLevelLabel();

        card.setListener(this.listener::onBugemonClicked);

        return card;
    }

    public interface Listener {

        void onBugemonClicked(PlayerBugemonDTO playerBugemonList);

    }
}
