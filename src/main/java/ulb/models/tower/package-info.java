/**
 * Tower roguelike structure. A run consists of {@value ulb.Configuration.Game#FLOOR_MAX} floors; each floor is a stack
 * of {@link ulb.models.tower.room.Room}s popped in order. Completing floor 8 (0-indexed) ends the run as a victory.
 */
package ulb.models.tower;
