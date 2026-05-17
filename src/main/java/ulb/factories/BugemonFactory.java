package ulb.factories;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.ElementType;
import ulb.models.player.BonusStats;
import ulb.models.player.PlayerBugemon;
import ulb.repositories.dto.CreateBugemonDTO;
import ulb.repositories.dto.PlayerBugemonDTO;
import ulb.repositories.dto.StaticBugemonDataDTO;

/**
 * Factory to create {@link ulb.models.bugemon.Bugemon} instances.
 */
public class BugemonFactory {

    /**
     * Private constructor to prevent instantiation.
     */
    private BugemonFactory() {
    }

    public static Bugemon createBugemon(CreateBugemonDTO bugemon) {
        String fileName;
        try {
            URI path = bugemon.spriteUrl().toURI();
            fileName = Paths.get(path).getFileName().toString();
        } catch (URISyntaxException e) {
            String urlStr = bugemon.spriteUrl().toString();
            fileName = urlStr.substring(urlStr.lastIndexOf('/') + 1);
        }

        List<Attack> attacks = new ArrayList<>();
        attacks.add(bugemon.attack1());
        attacks.add(bugemon.attack2());
        attacks.add(bugemon.attack3());
        return new Bugemon(bugemon.name(), bugemon.maxHp(), bugemon.attack(), bugemon.defense(), bugemon.initiative(),
                bugemon.type(), attacks, fileName, bugemon.isStarter());
    }

    public static PlayerBugemon createPlayerBugemon(StaticBugemonDataDTO defaultBugemon,
            PlayerBugemonDTO playerBugemon) {
        Bugemon base = new Bugemon(defaultBugemon.name(), playerBugemon.currentMaxHp(),
                playerBugemon.currentAttackPower(), playerBugemon.currentDefense(), playerBugemon.currentInitiative(),
                ElementType.valueOf(defaultBugemon.type()), defaultBugemon.attackList(), defaultBugemon.spriteUrl(),
                defaultBugemon.isStarter());
        BonusStats bonusStats = new BonusStats(playerBugemon.currentMaxHp(), playerBugemon.currentAttackPower(),
                playerBugemon.currentDefense(), playerBugemon.currentInitiative());
        return new PlayerBugemon(base, playerBugemon.currentLevel(), playerBugemon.currentXp(), bonusStats,
                defaultBugemon.attackList());
    }

    /**
     * Create a PlayerBugemon with default values. Used when a player has never played with the bugemon.
     *
     * @param base
     *            the base bugemon
     * @return the PlayerBugemon
     */
    public static PlayerBugemon createPlayerBugemon(Bugemon base, PlayerBugemonDTO playerBugemon) {
        BonusStats bonusStats = new BonusStats(playerBugemon.currentMaxHp(), playerBugemon.currentAttackPower(),
                playerBugemon.currentDefense(), playerBugemon.currentInitiative());
        return new PlayerBugemon(base, playerBugemon.currentLevel(), playerBugemon.currentXp(), bonusStats,
                base.attacks()); // TODO: need to change base.attacks with the list of attacks of the playerBugemonDTO
    }

    /**
     * Create a PlayerBugemon with default values. Used when a player has never played with the bugemon.
     *
     * @param base
     *            the base bugemon
     * @return the PlayerBugemon
     */
    public static PlayerBugemon createDefaultPlayerBugemon(Bugemon base) {
        BonusStats bonusStats = new BonusStats(0, 0, 0, 0);
        return new PlayerBugemon(base, 1, 0, bonusStats, base.attacks());
    }

    public static StaticBugemonDataDTO createStaticBugemonData(Bugemon bugemon) {
        return new StaticBugemonDataDTO(bugemon.name(), bugemon.type().name(), bugemon.spritePath(), bugemon.attacks(),
                bugemon.isStarter());
    }
}
