package ulb.factories;

import java.net.URI;
import java.nio.file.Paths;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonBuilder;
import ulb.models.bugemon.BugemonType;
import ulb.repositories.dto.CreateBugemonDTO;
import ulb.repositories.dto.PlayerBugemonDTO;
import ulb.repositories.dto.StaticBugemonDataDTO;

/** Creates {@link ulb.models.bugemon.Bugemon} instances from the various DTO types used by repositories. */
public class BugemonFactory {

    private BugemonFactory() {
    }

    /** Creates a fresh level-1 Bugemon from a player-created DTO; extracts the sprite file name from the URL. */
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

    /** Reconstructs a Bugemon from its static definition and the player's saved progression stats. */
    public static Bugemon createBugemon(StaticBugemonDataDTO defaultBugemon, PlayerBugemonDTO playerBugemon) {
        return new BugemonBuilder().name(defaultBugemon.name()).type(BugemonType.valueOf(defaultBugemon.type()))
                .sprite(defaultBugemon.spriteUrl()).hp(playerBugemon.currentMaxHp())
                .attack(playerBugemon.currentAttackPower()).defense(playerBugemon.currentDefense())
                .initiative(playerBugemon.currentInitiative()).xp(playerBugemon.currentXp())
                .level(playerBugemon.currentLevel()).attackList(defaultBugemon.attackList())
                .isStarter(defaultBugemon.isStarter()).build();
    }

    /** Snapshots the static (level-1) fields of a Bugemon into a {@link StaticBugemonDataDTO}. */
    public static StaticBugemonDataDTO createStaticBugemonData(Bugemon bugemon) {
        return new StaticBugemonDataDTO(bugemon.getName(), bugemon.getType().name(), bugemon.getSpriteURL(),
                bugemon.getAttackList(), bugemon.isStarter());
    }
}
