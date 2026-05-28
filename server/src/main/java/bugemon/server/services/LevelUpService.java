package bugemon.server.services;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import bugemon.common.Configuration;
import bugemon.common.models.player.BonusStats;
import bugemon.common.models.player.BonusStatsGenerator;
import bugemon.common.models.run.RunBugemon;
import bugemon.server.repositories.BugemonRepository;

/**
 * Service that handles Bugemon level-up logic during a tower run.
 *
 * <p>
 * Generates a set of randomised {@link bugemon.common.models.player.BonusStats} options for the player to choose from,
 * then applies the chosen bonus and persists the updated {@link bugemon.common.models.run.RunBugemon} state.
 */
public class LevelUpService {
    String playerName;
    BugemonRepository bugemonRepository;
    BonusStatsGenerator bonusStatsGenerator;

    /**
     * Constructs a {@code LevelUpService} bound to the given player.
     *
     * @param bugemonRepository
     *            repository used to persist updated Bugemon stats after a level-up
     * @param random
     *            random-number source for generating bonus stat options
     * @param playerName
     *            the name of the player whose Bugemon data is persisted
     */
    public LevelUpService(BugemonRepository bugemonRepository, Random random, String playerName) {
        this.playerName = playerName;
        this.bugemonRepository = bugemonRepository;
        this.bonusStatsGenerator = new BonusStatsGenerator(random);
    }

    /**
     * Applies the chosen {@code bonus} to {@code bugemon} and persists the resulting stat changes.
     *
     * @param bugemon
     *            the Bugemon receiving the level-up bonus
     * @param bonus
     *            the stat bonus chosen by the player
     */
    public void applyLevelUp(RunBugemon bugemon, BonusStats bonus) {
        bugemon.applyBonus(bonus);
        this.bugemonRepository.save(bugemon.getPlayerBugemon().toDTO(this.playerName));
    }

    /**
     * Generates a list of randomised {@link bugemon.common.models.player.BonusStats} for the player to choose from. The
     * number of options is determined by {@link bugemon.common.Configuration.Game#NUM_BONUS_PER_LEVEL_UP}.
     *
     * @return an immutable list of bonus-stat options
     */
    public List<BonusStats> generateLevelUpOptions() {
        return IntStream.range(0, Configuration.Game.NUM_BONUS_PER_LEVEL_UP)
                .mapToObj(i -> this.bonusStatsGenerator.generateBonusStats()).toList();
    }
}
