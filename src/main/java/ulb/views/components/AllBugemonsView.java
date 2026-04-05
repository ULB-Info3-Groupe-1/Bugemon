package ulb.views.components;

import java.util.List;
import java.util.function.Function;
import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;

import ulb.models.bugemon.Bugemon;

/** Reusable custom component displaying all the bugemons inside of a grid. */
public class AllBugemonsView extends ComponentView {
    private static final String FXML_PATH = "/fxml/components/AllBugemons.fxml";

    @FXML
    private FlowPane flowPane;

    Listener listener;

    private Function<Bugemon, Boolean> selectionChecker;

    public AllBugemonsView() {
        super(FXML_PATH);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setSelectionChecker(Function<Bugemon, Boolean> checker) {
        this.selectionChecker = checker;
    }

    /** Clears and repopulates the grid with the given list of Bugemons. */
    public void showAll(List<Bugemon> bugemonList) {
        this.flowPane.getChildren().clear();

        if (bugemonList.isEmpty()) {
            return;
        }

        for (Bugemon bugemon : bugemonList) {
            this.flowPane.getChildren().add(this.createBugemonCard(bugemon));
        }
    }

    private BugemonCardView createBugemonCard(Bugemon bugemon) {
        BugemonCardView card = new BugemonCardView(bugemon);
        card.hideLevelLabel();

        if (this.selectionChecker != null) {
            card.setSelected(this.selectionChecker.apply(bugemon));
        }

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
