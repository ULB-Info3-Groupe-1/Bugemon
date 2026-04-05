/**
 * Component-based stat system for {@link ulb.models.bugemon.Bugemon}. Each stat (HP, attack, defense, initiative,
 * level) is encapsulated in a subclass of {@link ulb.models.bugemon.components.AbstractComponent}. Temporary
 * modifiers are stacked via {@code addModifier(Modifier)} and decremented each turn by {@code tick()}, which also
 * removes expired ones. {@code clearModifiers()} resets all active modifiers immediately (e.g. end of combat).
 */
package ulb.models.bugemon.components;
