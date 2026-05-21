package ulb.controllers;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import ulb.common.LevelUpResult;
import ulb.models.player.BonusStats;
import ulb.services.LevelUpService;
import ulb.views.LevelUpView;
import ulb.views.ViewLoader;

public class LevelUpController extends Controller<LevelUpView> implements LevelUpView.Listener {
    private final LevelUpService levelUpService;
    private final Queue<LevelUpResult> pendingLevelUps;

    private LevelUpResult currentLevelUp;

    public LevelUpController(MetaController metaController, LevelUpService levelUpService) {
        super(metaController, ViewLoader.load(LevelUpView::new));
        this.levelUpService = levelUpService;
        this.pendingLevelUps = new LinkedList<>();
        this.view.setListener(this);
    }

    public void initialize(List<LevelUpResult> levelUps) {
        this.pendingLevelUps.clear();
        this.pendingLevelUps.addAll(levelUps);
        this.processNextLevelUp();
    }

    public void processNextLevelUp() {
        if (this.pendingLevelUps.isEmpty()) {
            this.metaController.onAllPendingLevelUpsConsumed();
            return;
        }
        this.currentLevelUp = this.pendingLevelUps.poll();
        this.view.displayLevelUpOptions(this.currentLevelUp.bugemon().getName(), this.currentLevelUp.levelPassed(),
                this.currentLevelUp.bugemon().getSpritePath(), this.levelUpService.generateLevelUpOptions());
    }

    @Override
    public void onBonusChosen(BonusStats bonus) {
        this.levelUpService.applyLevelUp(this.currentLevelUp.bugemon(), bonus);
        this.processNextLevelUp();
    }
}
