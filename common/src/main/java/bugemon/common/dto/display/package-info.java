/**
 * Display DTOs: immutable snapshots built by services and consumed by views.
 *
 * <p>
 * Each record contains only the data required to render a specific screen, preventing views from reaching into domain
 * model internals. Services construct these DTOs and pass them to controllers, which forward them to the JavaFX views.
 *
 * <p>
 * Key records in this package:
 * <ul>
 * <li>{@link bugemon.common.dto.display.BugemonDisplayDTO} — full Bugemon stats for the collection and team-management
 * screens</li>
 * <li>{@link bugemon.common.dto.display.RunBugemonDisplayDTO} — minimal combat-HUD snapshot</li>
 * <li>{@link bugemon.common.dto.display.FloorDisplayDTO} — complete floor layout for the map view</li>
 * <li>{@link bugemon.common.dto.display.RoomDisplayDTO} — single room position, type and state</li>
 * <li>{@link bugemon.common.dto.display.ConnectionDisplayDTO} — grid coordinates of an edge between two rooms</li>
 * </ul>
 */
package bugemon.common.dto.display;
