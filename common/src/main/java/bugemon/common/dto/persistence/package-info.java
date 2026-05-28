/**
 * Persistence DTOs: plain records that carry raw data between the repository and service layers.
 *
 * <p>
 * These records are never exposed above the service layer; controllers and views should work with domain model objects
 * instead. Each DTO corresponds closely to one or more database tables and is used solely for reading from or writing
 * to the persistence store.
 *
 * <p>
 * Key records in this package:
 * <ul>
 * <li>{@link bugemon.common.dto.persistence.TowerDTO} — full run-state snapshot (seed + floor + team)</li>
 * <li>{@link bugemon.common.dto.persistence.TeamDTO} / {@link bugemon.common.dto.persistence.TeamMemberDTO} — team roster
 * persistence</li>
 * <li>{@link bugemon.common.dto.persistence.PlayerBugemonDTO} — per-player Bugemon progression</li>
 * <li>{@link bugemon.common.dto.persistence.InventoryDTO} / {@link bugemon.common.dto.persistence.DefaultInventoryDTO} — item
 * inventory persistence</li>
 * <li>{@link bugemon.common.dto.persistence.SkillDTO} — individual skill-tree node state</li>
 * <li>{@link bugemon.common.dto.persistence.CreateBugemonDTO} — seed-time Bugemon definition</li>
 * </ul>
 */
package bugemon.common.dto.persistence;
