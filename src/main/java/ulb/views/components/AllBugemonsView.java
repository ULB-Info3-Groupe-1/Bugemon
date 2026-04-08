package ulb.views.components;

import java.util.List;
import java.util.Set;
import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;

import ulb.models.bugemon.Bugemon;

/** Reusable custom component displaying all the bugemons inside of a grid. */
public class AllBugemonsView extends ComponentView {
    private static final String FXML_PATH = "/fxml/components/AllBugemons.fxml";

    @FXML
    private FlowPane flowPane;

    private Listener listener;

    public AllBugemonsView() {
        super(FXML_PATH);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    /** Clears and repopulates the grid with the given list of Bugemons. */
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

        card.setListener(new BugemonCardView.Listener() {

            @Override
            public void onClick(Bugemon bugemon) {
                AllBugemonsView.this.listener.onBugemonClicked(bugemon);
            }

        });

        return card;
    }

    public interface Listener {

        void onBugemonClicked(Bugemon bugemon);

    }
}
