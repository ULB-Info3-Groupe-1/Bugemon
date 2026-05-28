package bugemon.client.controllers;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

import bugemon.common.Configuration;
import bugemon.common.dto.persistence.CreateBugemonDTO;
import bugemon.common.models.bugemon.Attack;
import bugemon.common.models.bugemon.ElementType;
import bugemon.server.repositories.exceptions.BugemonNameIsEmptyException;
import bugemon.server.services.BugemonService;
import bugemon.server.services.exceptions.BugemonNameAlreadyExistsException;
import bugemon.client.views.CreateBugemonView;
import bugemon.client.views.ViewLoader;

/**
 * Controller for the custom Bugemon creation screen.
 *
 * <p>
 * Manages element-type and sprite selection state, validates form input, assembles a
 * {@link bugemon.common.dto.persistence.CreateBugemonDTO}, and delegates persistence to
 * {@link bugemon.server.services.BugemonService}.
 */
public class CreateBugemonController extends Controller<CreateBugemonView> implements CreateBugemonView.Listener {

    private final BugemonService bugemonService;

    private Optional<ElementType> selectedBugemonType = Optional.empty();
    private Optional<URL> selectedBugemonSpriteUrl = Optional.empty();

    public CreateBugemonController(MetaController metaController, BugemonService bugemonService) {
        super(metaController, ViewLoader.load(CreateBugemonView::new));
        this.bugemonService = bugemonService;
        this.view.setListener(this);
    }

    @Override
    public void onTypeSelected(ElementType selectedType) {
        this.selectedBugemonType = Optional.of(selectedType);
        this.updateAvailableAttacks();
    }

    @Override
    public void onSpriteSelected(File selectedSpriteFile) {
        try {
            this.selectedBugemonSpriteUrl = Optional.of(selectedSpriteFile.toURI().toURL());
            this.view.setSprite(selectedSpriteFile);
        } catch (MalformedURLException e) {
            this.selectedBugemonSpriteUrl = Optional.empty();
        }
    }

    /**
     * Refreshes the view's attack selection list to show only attacks matching the currently selected element type.
     * Does nothing if no type has been selected yet.
     */
    public void updateAvailableAttacks() {
        this.selectedBugemonType.ifPresent(bugemonType -> {
            List<Attack> attacks = this.bugemonService.getAttacks(bugemonType);
            this.view.setAvailableAttacks(attacks);
        });
    }

    @Override
    public void onAdd(String bugemonName, double healthValue, double attackValue, double defenseValue,
            double initiativeValue, List<Attack> attacks) {
        int hp = (int) Math.round(healthValue);
        int attack = (int) Math.round(attackValue);
        int defense = (int) Math.round(defenseValue);
        int initiative = (int) Math.round(initiativeValue);

        if (attacks.size() != Configuration.Game.NUM_ATTACKS_PER_BUGEMON) {
            this.view.showInvalidFormChooseAttacks();
            return;
        }

        if (this.selectedBugemonSpriteUrl.isEmpty()) {
            this.view.showInvalidFormChooseSprite();
            return;
        }

        if (this.selectedBugemonType.isEmpty()) {
            this.view.showInvalidFormChooseBugemonType();
            return;
        }

        CreateBugemonDTO bugemonToCreate = this.bugemonService.createBugemon(bugemonName,
                this.selectedBugemonType.get(), this.selectedBugemonSpriteUrl.get(), attack, defense, initiative, hp,
                attacks);

        try {
            this.bugemonService.saveNewBugemon(bugemonToCreate);
            this.view.showSaveSuccessAlert(bugemonName);
        } catch (BugemonNameIsEmptyException e) {
            this.view.showBugemonNameEmptyAlert();
        } catch (BugemonNameAlreadyExistsException e) {
            this.view.showBugemonNameAlreadyUsedAlert();
        }
    }

    @Override
    public void onReturnToMainMenu() {
        this.metaController.onMainMenu();
    }
}
