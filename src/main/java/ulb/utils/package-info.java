/**
 * JSON parsing pipeline that loads game data files into model objects at startup.
 *
 * JSON field names follow French conventions ({@code nom}, {@code attaques}, …); Gson {@code @SerializedName}
 * annotations bridge them to Java names. Sprite paths without a {@code "png/"} prefix are automatically prefixed by
 * {@link ulb.utils.BugemonDeserializer}.
 */
package ulb.utils;
