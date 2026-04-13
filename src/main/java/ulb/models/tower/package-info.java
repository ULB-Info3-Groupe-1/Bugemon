/**
 * Tower roguelike structure. A run consists of 9 sequential {@link ulb.models.tower.Floor}s; each floor is a
 * procedurally generated tree of {@link ulb.models.tower.FloorNode}s. The player navigates node by node and must reach
 * the boss node to complete the floor. Completing all floors ends the run as a victory.
 */
package ulb.models.tower;
