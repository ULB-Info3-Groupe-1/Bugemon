/**
 * Business logic layer between controllers and the repository.
 *
 * {@link ulb.services.PlayerService} is the main entry point: it owns the player's runtime state (active team, team
 * list, inventory) and delegates all persistence to {@link ulb.repositories.PlayerRepository}.
 * {@link ulb.services.CombatService} is stateless and handles damage formulas, type effectiveness, and initiative
 * priority. {@link ulb.services.InventoryService} manages item creation.
 */
package ulb.services;
