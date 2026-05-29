package bugemon.common.net;

import java.util.List;

import bugemon.common.models.bugemon.Attack;
import bugemon.common.models.bugemon.Bugemon;
import bugemon.common.models.item.Item;
import bugemon.common.models.skills.SkillTree;

/**
 * Server reply carrying the immutable game catalogue, fetched once at login and cached client-side.
 *
 * <p>
 * With this data the client can generate opponents, rewards and level-up options, and build skill contexts entirely
 * locally — only player-specific mutations travel back to the server.
 *
 * @param bugemons
 *            all species definitions (including bosses)
 * @param attacks
 *            all attack definitions
 * @param items
 *            all item definitions
 * @param skillTree
 *            the skill-tree definition
 */
public record StaticDataPacket(List<Bugemon> bugemons, List<Attack> attacks, List<Item> items,
        SkillTree skillTree) implements Packet {
}
