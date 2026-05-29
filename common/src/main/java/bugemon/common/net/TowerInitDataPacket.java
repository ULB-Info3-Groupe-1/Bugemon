package bugemon.common.net;

import java.util.List;

import bugemon.common.dto.persistence.TowerDTO;
import bugemon.common.models.item.Item;
import bugemon.common.models.skills.SkillTree;
import bugemon.common.models.team.Team;

/**
 * Server reply with the data needed to start or resume a tower run.
 *
 * <p>
 * If a saved run exists, {@code savedTower} and {@code savedTeam} are populated so the client can restore it via
 * {@link bugemon.common.models.tower.TowerEngine#restore}; otherwise both are {@code null} and the client creates a
 * fresh run. The static {@code items} and {@code skillTree} let the client apply skill-based starter-item bonuses
 * locally.
 *
 * @param savedTower
 *            the persisted run snapshot, or {@code null} if none
 * @param savedTeam
 *            the team matching the saved run, or {@code null} if none / not found
 * @param items
 *            all static item definitions
 * @param skillTree
 *            the static skill-tree definition
 */
public record TowerInitDataPacket(TowerDTO savedTower, Team savedTeam, List<Item> items,
        SkillTree skillTree) implements Packet {
}
