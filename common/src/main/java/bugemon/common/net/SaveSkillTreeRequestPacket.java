package bugemon.common.net;

import bugemon.common.models.skills.SkillTreeState;

/**
 * Request asking the server to persist the player's skill-tree progression.
 *
 * <p>
 * Skill-point allocation is validated and applied locally on the client (pure model logic on {@link SkillTreeState});
 * this packet only ships the resulting state to be saved. The reply is an {@link AckPacket}.
 *
 * @param skillTreeState
 *            the up-to-date skill-tree state to persist
 */
public record SaveSkillTreeRequestPacket(SkillTreeState skillTreeState) implements Packet {
}
