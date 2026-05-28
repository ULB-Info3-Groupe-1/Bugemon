package bugemon.server.repositories;

import java.util.List;
import java.util.Optional;

import bugemon.common.dto.persistence.CreateBugemonDTO;
import bugemon.common.dto.persistence.DefaultInventoryDTO;
import bugemon.common.models.bugemon.Attack;
import bugemon.common.models.bugemon.Bugemon;
import bugemon.common.models.item.Item;
import bugemon.common.models.skills.SkillTree;

/**
 * Repository for immutable, game-wide static data: Bugemon archetypes, attacks, items, the skill tree, and the default
 * inventory granted to new players.
 *
 * <p>
 * Implementations typically cache all data eagerly on construction because these records are read-only at runtime
 * (except for admin-level {@link #saveBugemon} operations).
 */
public interface StaticRepository {

    /**
     * Returns all Bugemon archetypes known to the game.
     *
     * @return unmodifiable list of base {@link Bugemon} instances
     */
    List<Bugemon> bugemons();

    /**
     * Looks up a single Bugemon archetype by its canonical name.
     *
     * @param name
     *            the Bugemon's canonical name
     * @return an {@link Optional} containing the archetype, or empty if unknown
     */
    Optional<Bugemon> findBugemonByName(String name);

    /**
     * Persists a new Bugemon archetype and refreshes the in-memory cache.
     *
     * @param dto
     *            the creation data for the new Bugemon
     */
    void saveBugemon(CreateBugemonDTO dto);

    /**
     * Returns all {@link Attack} instances known to the game.
     *
     * @return unmodifiable list of attacks
     */
    List<Attack> attacks();

    /**
     * Returns the default inventory configuration granted to newly created players.
     *
     * @return the {@link DefaultInventoryDTO} with starting items and quantities
     */
    DefaultInventoryDTO defaultInventory();

    /**
     * Returns the full skill tree definition used across all players.
     *
     * @return the shared {@link SkillTree}
     */
    SkillTree skillTree();

    /**
     * Returns all items defined in the game.
     *
     * @return unmodifiable list of {@link Item} instances
     */
    List<Item> items();
}
