package ulb.factory;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon.BugemonBuilder;
import ulb.repository.dto.CreateBugemonDTO;

/**
 * Factory to create {@link ulb.models.bugemon.Bugemon} instances.
 */
public class BugemonFactory {

    /**
     * Private constructor to prevent instantiation.
     */
    private BugemonFactory() {
    }

    public static Bugemon create(CreateBugemonDTO bugemon) {
        return new BugemonBuilder().name(bugemon.name()).type(bugemon.type()).sprite(bugemon.spriteUrl().toString())
                .defense(bugemon.defense()).attack(bugemon.attack()).initiative(bugemon.initiative())
                .hp(bugemon.maxHp()).isStarter(bugemon.isStarter()).addAttack(bugemon.attack1())
                .addAttack(bugemon.attack2()).addAttack(bugemon.attack3()).build();
    }

}
