/**
 * Model layer of the MVC architecture. Contains no classes directly; all types live in sub-packages ({@code bugemon},
 * {@code bugemon_team}, {@code trainer}, {@code combat}).
 *
 * The model is deliberately decoupled from JavaFX; data consumed by views is exposed through
 * {@link ulb.common.dto.BugemonDTO} rather than the full model class.
 */
package ulb.models;
