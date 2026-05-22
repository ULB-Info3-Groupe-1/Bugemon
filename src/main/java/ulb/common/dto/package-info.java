/**
 * Data Transfer Object (DTO) hierarchy used to move data between application layers without exposing domain internals.
 *
 * <p>
 * DTOs in this package tree are plain Java records with no business logic. They are organised into two sub-packages:
 * <ul>
 * <li>{@link ulb.common.dto.display} — read-only snapshots consumed by controllers and views to render UI screens</li>
 * <li>{@link ulb.common.dto.persistence} — records used by repositories to read from and write to the database</li>
 * </ul>
 */
package ulb.common.dto;
