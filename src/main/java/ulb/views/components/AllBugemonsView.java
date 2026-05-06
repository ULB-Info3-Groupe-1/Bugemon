package ulb.views.components;

import java.util.List;
import java.util.Set;
import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;

import ulb.Configuration;
import ulb.models.bugemon.Bugemon;

/** Reusable custom component displaying all the bugemons inside of a grid. */
public class AllBugemonsView extends ComponentView {

    @FXML
    private FlowPane flowPane;

    private Listener listener;

    /**
     * Default constructor.
     */
    public AllBugemonsView() {
        super(Configuration.Paths.Fxml.COMPONENT_ALL_BUGEMONS);
    }

    /**
     * Sets the listener to be notified when a Bugemon is clicked.
     *
     * @param listener
     *            the listener
     */
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    /**
     * Clears and repopulates the grid with the given list of Bugemons.
     *
     * @param bugemonList
     *            the list of Bugemons to display
     * @param selectedBugemons
     *            the set of selected Bugemons
     */
    public void showAll(List<Bugemon> bugemonList, Set<Bugemon> selectedBugemons) {
        this.flowPane.getChildren().clear();

        for (Bugemon bugemon : bugemonList) {
            BugemonCardView bugemonCard = this.createBugemonCard(bugemon);

            if (selectedBugemons.contains(bugemon)) {
                bugemonCard.select();
            } else {
                bugemonCard.unselect();
            }

            this.flowPane.getChildren().add(bugemonCard);
        }
    }

    private BugemonCardView createBugemonCard(Bugemon bugemon) {
        BugemonCardView card = new BugemonCardView(bugemon);
        card.hideLevelLabel();

        card.setListener(this.listener::onBugemonClicked);

        return card;
    }

    public interface Listener {

        /**
         * Dispatches a click event to the controller.
         *
         * @param bugemon
         *            the clicked Bugemon
         */
        void onBugemonClicked(Bugemon bugemon);

    }
}
