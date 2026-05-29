package bugemon.common.net;

import bugemon.common.models.skills.SkillTree;

/**
 * Server reply carrying the static skill-tree definition requested via {@link GetSkillTreeRequestPacket}.
 *
 * @param skillTree
 *            the full skill-tree graph used by the client to render the screen and validate point allocation
 */
public record SkillTreeResponsePacket(SkillTree skillTree) implements Packet {
}
