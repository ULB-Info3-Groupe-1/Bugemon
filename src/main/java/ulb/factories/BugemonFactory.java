package ulb.factories;

import java.net.URI;
import java.nio.file.Paths;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonBuilder;
import ulb.models.bugemon.BugemonType;
import ulb.repositories.dto.CreateBugemonDTO;
import ulb.repositories.dto.PlayerBugemonDTO;
import ulb.repositories.dto.StaticBugemonDataDTO;

/**
 * Factory to create {@link ulb.models.bugemon.Bugemon} instances.
 */
public class BugemonFactory {

    private BugemonFactory() {
        // Private constructor to prevent instantiation.
    }

    /**
     * Create a {@link ulb.models.bugemon.Bugemon} instance from a {@link CreateBugemonDTO}.
     *
     * @param bugemon
     *            the {@link CreateBugemonDTO} object containing the bugemon data
     * @return the created {@link ulb.models.bugemon.Bugemon}
     */
    public static Bugemon createBugemon(CreateBugemonDTO bugemon) {
        String fileName;
        try {
            URI path = bugemon.spriteUrl().toURI();
            fileName = Paths.get(path).getFileName().toString();
        } catch (Exception e) {
            String urlStr = bugemon.spriteUrl().toString();
            fileName = urlStr.substring(urlStr.lastIndexOf('/') + 1);
        }
        return new BugemonBuilder().name(bugemon.name()).type(bugemon.type()).sprite(fileName)
                .defense(bugemon.defense()).attack(bugemon.attack()).initiative(bugemon.initiative())
                .hp(bugemon.maxHp()).isStarter(bugemon.isStarter()).addAttack(bugemon.attack1())
                .addAttack(bugemon.attack2()).addAttack(bugemon.attack3()).build();
    }

    /**
     * Create a {@link ulb.models.bugemon.Bugemon} instance from a {@link StaticBugemonDataDTO} and a
     * {@link PlayerBugemonDTO}.
     *
     * @param defaultBugemon
     *            the {@link StaticBugemonDataDTO} object containing the default bugemon data
     * @param playerBugemon
     *            the {@link PlayerBugemonDTO} object containing the player bugemon data
     * @return the created {@link ulb.models.bugemon.Bugemon}
     */
    public static Bugemon createBugemon(StaticBugemonDataDTO defaultBugemon, PlayerBugemonDTO playerBugemon) {
        return new BugemonBuilder().name(defaultBugemon.name()).type(BugemonType.valueOf(defaultBugemon.type()))
                .sprite(defaultBugemon.spriteUrl()).hp(playerBugemon.currentMaxHp())
                .attack(playerBugemon.currentAttackPower()).defense(playerBugemon.currentDefense())
                .initiative(playerBugemon.currentInitiative()).xp(playerBugemon.currentXp())
                .level(playerBugemon.currentLevel()).attackList(defaultBugemon.attackList())
                .isStarter(defaultBugemon.isStarter()).build();
    }

    /**
     * Create a {@link StaticBugemonDataDTO} instance from a {@link ulb.models.bugemon.Bugemon}.
     *
     * @param bugemon
     *            the {@link ulb.models.bugemon.Bugemon} object containing the bugemon data
     * @return the created {@link StaticBugemonDataDTO}
     */
    public static StaticBugemonDataDTO createStaticBugemonData(Bugemon bugemon) {
        return new StaticBugemonDataDTO(bugemon.getName(), bugemon.getType().name(), bugemon.getSpriteURL(),
                bugemon.getAttackList(), bugemon.isStarter());
    }
}
