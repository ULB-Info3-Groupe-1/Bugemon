package ulb.services.game;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ulb.common.dto.persistence.SkillDTO;
import ulb.models.skills.SkillContext;
import ulb.models.skills.SkillTree;
import ulb.models.skills.SkillTreeState;
import ulb.models.skills.exceptions.IllegalNodeStateException;
import ulb.repositories.SkillRepository;
import ulb.repositories.StaticRepository;

/**
 * Service that manages the player's skill tree state.
 *
 * <p>
 * Bridges {@link ulb.models.skills.SkillTreeState} (mutable runtime state) with the static
 * {@link ulb.models.skills.SkillTree} definition loaded from JSON. Provides methods for loading, saving, and modifying
 * skill-node levels, and for building a {@link ulb.models.skills.SkillContext} that exposes active bonus values to
 * other services.
 */
public class SkillService {

    private final String playerName;

    private final SkillRepository skillRepository;
    private final StaticRepository staticRepository;

    /**
     * Constructs a {@code SkillService} bound to the given player.
     *
     * @param skillRepository
     *            repository for reading and writing per-player skill levels and points
     * @param staticRepository
     *            repository providing the static {@link ulb.models.skills.SkillTree} definition
     * @param playerName
     *            the name of the player whose skill data this service manages
     */
    public SkillService(SkillRepository skillRepository, StaticRepository staticRepository, String playerName) {
        this.skillRepository = skillRepository;
        this.staticRepository = staticRepository;

        this.playerName = playerName;
    }

    public void save(SkillTreeState skillTreeState) {
        this.skillRepository.save(this.playerName, this.toDTO(skillTreeState), skillTreeState.getSkillPoints());
    }

    /**
     * Loads the player's skill-tree state from the repository, restoring node levels and available skill points.
     *
     * @return the restored {@link ulb.models.skills.SkillTreeState}
     */
    public SkillTreeState getSkillTreeState() {
        List<SkillDTO> dtos = this.skillRepository.findAll(this.playerName);
        int skillPoints = this.skillRepository.findSkillPoints(this.playerName);
        Map<String, Integer> levels = dtos.stream().collect(Collectors.toMap(SkillDTO::skillId, SkillDTO::level));
        return SkillTreeState.restore(levels, skillPoints);
    }

    public SkillTree getSkillTree() {
        return this.staticRepository.skillTree();
    }

    /**
     * Builds a {@link ulb.models.skills.SkillContext} from {@code state} and the static skill tree. The context is a
     * read-only view of active bonuses used by combat and inventory services.
     *
     * @param state
     *            the current skill-tree state
     * @return a {@code SkillContext} reflecting the active bonuses
     */
    public SkillContext buildSkillContext(SkillTreeState state) {
        return new SkillContext(state, this.getSkillTree());
    }

    /**
     * Spends one skill point to increase the level of the node identified by {@code nodeId}.
     *
     * @param skillTreeState
     *            the mutable runtime state to update
     * @param skillTree
     *            the static tree definition used to validate prerequisites
     * @param nodeId
     *            the identifier of the node to level up
     * @throws ulb.models.skills.exceptions.IllegalNodeStateException
     *             if prerequisites are not met or the node is already at maximum level
     */
    public void addPoint(SkillTreeState skillTreeState, SkillTree skillTree, String nodeId)
            throws IllegalNodeStateException {
        skillTreeState.addPoint(nodeId, skillTree);
    }

    /**
     * Refunds one skill point by decreasing the level of the node identified by {@code nodeId}.
     *
     * @param skillTreeState
     *            the mutable runtime state to update
     * @param skillTree
     *            the static tree definition used to validate dependants
     * @param nodeId
     *            the identifier of the node to level down
     * @throws ulb.models.skills.exceptions.IllegalNodeStateException
     *             if removing the point would leave a dependent node without its prerequisite
     */
    public void removePoint(SkillTreeState skillTreeState, SkillTree skillTree, String nodeId)
            throws IllegalNodeStateException {
        skillTreeState.removePoint(nodeId, skillTree);
    }

    private List<SkillDTO> toDTO(SkillTreeState skillTreeState) {
        return skillTreeState.getSkillLevels().entrySet().stream()
                .map(entry -> new SkillDTO(entry.getKey(), entry.getValue())).toList();
    }

}
