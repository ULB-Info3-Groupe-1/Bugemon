package ulb.views.combat;

import java.io.IOException;

/**
 * View for the automatic combat screen, where both sides act without any
 * human input.
 *
 * <p>
 * {@code AutomaticCombatView} extends {@link CombatView} and configures the
 * shared combat layout for a fully automated session: the action menu and the
 * Bugemon team panel are hidden, since the player never needs to select an
 * action or switch manually during an automatic combat.
 * </p>
 *
 * <p>
 * The view is driven exclusively by
 * {@link ulb.controllers.combat.AutomaticCombatController}, which updates the
 * Bugemon info widgets and the dialog zone after each turn via the methods
 * inherited from {@link CombatView}.
 * </p>
 *
 * @see CombatView
 * @see ulb.controllers.combat.AutomaticCombatController
 */
public class AutomaticCombatView extends CombatView {
    /**
     * Constructs an {@code AutomaticCombatView}, loads the shared combat FXML
     * layout, and immediately calls {@link #initCombatMode()} to hide the
     * widgets that are not relevant to automatic combat.
     *
     * @throws IOException if the underlying {@link CombatView} fails to load
     *                     its FXML resource.
     */
    public AutomaticCombatView() throws IOException {
        super();
        this.initCombatMode();
    }

    /**
     * Configures the view for automatic combat by hiding the action menu and
     * the Bugemon team panel.
     *
     * <p>
     * Both nodes are removed from the layout (via {@code setManaged(false)}) as
     * well as made invisible, so they do not consume space in the scene graph
     * during the automated session.
     * </p>
     */
    @Override
    public void initCombatMode() {
        this.actionMenuView.setVisible(false);
        this.actionMenuView.setManaged(false);
        this.bugemonTeamPane.setVisible(false);
        this.bugemonTeamPane.setManaged(false);
    }
}
