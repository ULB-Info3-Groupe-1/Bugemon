package bugemon.client.controllers;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bugemon.client.services.RemoteBugemonService;
import bugemon.client.views.LevelUpView;
import bugemon.client.views.ViewLoader;
import bugemon.common.Configuration;
import bugemon.common.LevelUpResult;
import bugemon.common.models.player.BonusStats;
import bugemon.common.models.player.BonusStatsGenerator;

/**
 * Controller for the level-up screen shown after combat.
 *
 * <p>
 * Processes a queue of {@link LevelUpResult} entries one at a time, displaying randomised bonus-stat options (generated
 * locally) for each Bugemon that gained a level. The chosen bonus is applied locally and the Bugemon's progression is
 * persisted asynchronously through {@link RemoteBugemonService}. When the queue empties it notifies the
 * {@link MetaController} so the flow can continue.
 */
public class LevelUpController extends Controller<LevelUpView> implements LevelUpView.Listener {

    private static final Logger LOG = LoggerFactory.getLogger(LevelUpController.class);

    private final RemoteBugemonService bugemonService;
    private final BonusStatsGenerator bonusStatsGenerator;
    private final Queue<LevelUpResult> pendingLevelUps;

    private LevelUpResult currentLevelUp;

    public LevelUpController(MetaController metaController, RemoteBugemonService bugemonService) {
        super(metaController, ViewLoader.load(LevelUpView::new));
        this.bugemonService = bugemonService;
        this.bonusStatsGenerator = new BonusStatsGenerator(new Random());
        this.pendingLevelUps = new LinkedList<>();
        this.view.setListener(this);
    }

    /**
     * Loads the list of pending level-ups and presents the first one.
     *
     * @param levelUps
     *            the level-up results to process; may be empty
     */
    public void initialize(List<LevelUpResult> levelUps) {
        this.pendingLevelUps.clear();
        this.pendingLevelUps.addAll(levelUps);
        this.processNextLevelUp();
    }

    /**
     * Dequeues the next pending level-up and displays its options, or signals completion if the queue is empty.
     */
    public void processNextLevelUp() {
        if (this.pendingLevelUps.isEmpty()) {
            this.metaController.onAllPendingLevelUpsConsumed();
            return;
        }
        this.currentLevelUp = this.pendingLevelUps.poll();
        this.view.displayLevelUpOptions(this.currentLevelUp.bugemon().getName(), this.currentLevelUp.levelPassed(),
                this.currentLevelUp.bugemon().getSpritePath(), this.generateLevelUpOptions());
    }

    @Override
    public void onBonusChosen(BonusStats bonus) {
        this.currentLevelUp.bugemon().applyBonus(bonus);
        this.bugemonService.savePlayerBugemon(this.currentLevelUp.bugemon().getPlayerBugemon())
                .whenComplete((ignored, error) -> {
                    if (error != null) {
                        LOG.warn("Failed to persist level-up", error);
                    }
                });
        this.processNextLevelUp();
    }

    private List<BonusStats> generateLevelUpOptions() {
        return IntStream.range(0, Configuration.Game.NUM_BONUS_PER_LEVEL_UP)
                .mapToObj(i -> this.bonusStatsGenerator.generateBonusStats()).toList();
    }
}
