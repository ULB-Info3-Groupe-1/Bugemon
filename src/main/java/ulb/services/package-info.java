/**
 * Business logic layer, sitting between controllers and repositories.
 *
 * <p>
 * Each service is scoped to a single concern:
 * <ul>
 * <li>{@link ulb.services.BugemonService} — species data and per-player Bugemon progression</li>
 * <li>{@link ulb.services.TeamService} — team persistence and active-team selection</li>
 * <li>{@link ulb.services.InventoryService} — player inventory loading, saving, and starter bonuses</li>
 * <li>{@link ulb.services.LevelUpService} — level-up option generation and stat application</li>
 * <li>{@link ulb.services.SkillService} — skill-tree state persistence and context building</li>
 * <li>{@link ulb.services.TowerService} — tower run lifecycle (create, load, save, delete) and floor display</li>
 * <li>{@link ulb.services.MusicService} — background music and sound-effect playback</li>
 * <li>{@link ulb.services.SaveService} — facade for full save and new-game reset</li>
 * <li>{@link ulb.services.CombatService} — stateless damage formulas and type-effectiveness</li>
 * </ul>
 *
 * <p>
 * All persistence calls go through repositories in {@code ulb.repositories}.
 */
package ulb.services;
