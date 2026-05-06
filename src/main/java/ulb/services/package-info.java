/**
 * Business logic layer, sitting between controllers and the repository.
 *
 * {@link ulb.services.TeamService} is the main entry point. It owns the player's runtime state: active team, team list,
 * inventory, and a cache of all default Bugemons (loaded once on first access).
 *
 * {@link ulb.services.CombatService} is stateless and handles damage formulas, type effectiveness, and initiative
 * priority. {@link ulb.services.InventoryService} handles item management.
 */
package ulb.services;
