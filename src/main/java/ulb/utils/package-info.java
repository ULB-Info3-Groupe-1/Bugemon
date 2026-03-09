/**
 * Contains utility classes used to load and deserialise the Bugemon game's
 * JSON data files into the model layer.
 *
 * <h2>Package overview</h2>
 * <p>
 * All game content (Bugemons and their attacks) is defined in JSON resource
 * files bundled with the application. The classes in this package form a
 * thin parsing pipeline that reads those files and produces fully constructed
 * model objects ready for use by the rest of the application.
 * </p>
 *
 * <h2>Key classes</h2>
 * <ul>
 *   <li>{@link ulb.utils.Parser} — the main entry point for loading game data.
 *       Its static {@link ulb.utils.Parser#parse(java.io.InputStream, java.io.InputStream)}
 *       method reads an attacks JSON stream and a Bugemons JSON stream, builds
 *       an ID-to-{@link ulb.models.bugemon.Attack} map from the first, and then
 *       delegates Bugemon deserialisation to
 *       {@link ulb.utils.BugemonDeserializer}, returning a
 *       {@link ulb.utils.Parser.ParseResult} that bundles both collections for
 *       the caller.</li>
 *   <li>{@link ulb.utils.Parser.ParseResult} — an immutable value object
 *       returned by {@link ulb.utils.Parser#parse} that exposes the parsed
 *       attacks map (via {@link ulb.utils.Parser.ParseResult#getAttacksMap()})
 *       and the list of Bugemons (via
 *       {@link ulb.utils.Parser.ParseResult#getBugemonsList()}).</li>
 *   <li>{@link ulb.utils.BugemonDeserializer} — a custom
 *       {@link com.google.gson.JsonDeserializer} for
 *       {@link ulb.models.bugemon.Bugemon} objects. It reads the JSON fields
 *       ({@code id}, {@code nom}, {@code type}, {@code sprite}, {@code starter},
 *       {@code stats}, {@code attaques}), resolves attack references via the
 *       pre-built attacks map, and constructs each Bugemon through the
 *       {@link ulb.models.bugemon.Bugemon.Builder} fluent API.</li>
 * </ul>
 *
 * <h2>JSON format assumptions</h2>
 * <ul>
 *   <li>Field names in the data files follow French conventions
 *       (e.g., {@code nom}, {@code attaques}, {@code puissance}); Gson's
 *       {@code @SerializedName} annotations and the custom deserializers in
 *       this package bridge those names to their Java counterparts.</li>
 *   <li>Bugemon type values are matched case-insensitively against the
 *       {@link ulb.models.bugemon.Bugemon.BType} enum constants.</li>
 *   <li>Sprite paths that do not yet start with {@code "png/"} are
 *       automatically prefixed by {@link ulb.utils.BugemonDeserializer}.</li>
 * </ul>
 *
 * <h2>Design notes</h2>
 * <ul>
 *   <li>{@link ulb.utils.Parser} is a non-instantiable utility class: all
 *       public API is exposed through static methods.</li>
 *   <li>Parsing errors are currently logged to {@code System.out}/{@code System.err}
 *       and cause the affected method to return {@code null}. A future refactor
 *       should replace this with a proper logging framework and explicit
 *       exception propagation.</li>
 *   <li>Two stub methods — {@code parseObjects} and {@code parseSkillTree} —
 *       exist in {@link ulb.utils.Parser} as placeholders for planned features
 *       (in-game items and skill trees) that have not yet been implemented.</li>
 * </ul>
 *
 * @see ulb.models.bugemon.Bugemon
 * @see ulb.models.bugemon.Attack
 * @see ulb.controllers.MetaController
 */
package ulb.utils;
