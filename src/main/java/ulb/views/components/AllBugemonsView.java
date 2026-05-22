package ulb.views.components;

import java.util.List;
import java.util.Set;
import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;

import ulb.Configuration;
import ulb.common.dto.display.BugemonDisplayDTO;

/**
 * Reusable custom component displaying all available Bugemons in a scrollable flow grid. Selected Bugemons are visually
 * highlighted; clicks are forwarded through {@link Listener}.
 */
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

    /**
     * Clears and repopulates the grid, marking each card as selected or unselected.
     *
     * @param bugemonList
     *            the complete list of Bugemons to display
     * @param selectedBugemons
     *            the subset that should appear selected
     */
    public void showAll(List<BugemonDisplayDTO> bugemonList, Set<BugemonDisplayDTO> selectedBugemons) {
        this.flowPane.getChildren().clear();

        for (BugemonDisplayDTO bugemon : bugemonList) {
            BugemonCardView bugemonCard = this.createBugemonCard(bugemon);

            if (selectedBugemons.contains(bugemon)) {
                bugemonCard.select();
            } else {
                bugemonCard.unselect();
            }

            this.flowPane.getChildren().add(bugemonCard);
        }
    }

    private BugemonCardView createBugemonCard(BugemonDisplayDTO bugemon) {
        BugemonCardView card = new BugemonCardView(bugemon);
        card.hideLevelLabel();
        card.setListener(this.listener::onBugemonSelected);
        return card;
    }

    /** Callback interface for Bugemon grid interactions. */
    public interface Listener {
        /** Called when the player clicks a Bugemon card. */
        void onBugemonSelected(BugemonDisplayDTO bugemon);
    }
}
