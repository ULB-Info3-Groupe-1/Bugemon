package ulb.controllers;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

import ulb.Configuration;
import ulb.models.bugemon.Attack;
import ulb.models.bugemon.BugemonType;
import ulb.repositories.dto.CreateBugemonDTO;
import ulb.repositories.exceptions.BugemonNameIsEmptyException;
import ulb.services.BugemonService;
import ulb.services.exceptions.BugemonNameAlreadyExistsException;
import ulb.views.CreateBugemonView;
import ulb.views.ViewLoader;

public class CreateBugemonController extends Controller<CreateBugemonView> implements CreateBugemonView.Listener {

    private final BugemonService bugemonService;

    private Optional<BugemonType> selectedBugemonType = Optional.empty();
    private Optional<URL> selectedBugemonSpriteUrl = Optional.empty();

    public CreateBugemonController(MetaController metaController, BugemonService bugemonService) {
        super(metaController, ViewLoader.load(CreateBugemonView::new));
        this.bugemonService = bugemonService;
        this.view.setListener(this);
    }

    @Override
    public void onTypeSelected(BugemonType selectedType) {
        this.selectedBugemonType = Optional.of(selectedType);
        this.updateAvailableAttacks();
    }

    @Override
    public void onSpriteSelected(File selectedSpriteFile) {
        try {
            this.selectedBugemonSpriteUrl = Optional.of(selectedSpriteFile.toURI().toURL());
        } catch (MalformedURLException e) {
            this.selectedBugemonSpriteUrl = Optional.empty();
        }
    }

    public void updateAvailableAttacks() {
        this.selectedBugemonType.ifPresent(bugemonType -> {
            List<Attack> attacks = this.bugemonService.getAttacksByType(bugemonType);
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

        Attack attack1 = attacks.get(0);
        Attack attack2 = attacks.get(1);
        Attack attack3 = attacks.get(2);

        if (this.selectedBugemonSpriteUrl.isEmpty()) {
            this.view.showInvalidFormChooseSprite();
            return;
        }

        if (this.selectedBugemonType.isEmpty()) {
            this.view.showInvalidFormChooseBugemonType();
            return;
        }

        CreateBugemonDTO bugemonToCreate = new CreateBugemonDTO(bugemonName, this.selectedBugemonType.get(),
                this.selectedBugemonSpriteUrl.get(), defense, attack, initiative, hp, false, attack1, attack2, attack3);

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
