/**
 * Temporary stat modifiers. A {@link ulb.models.bugemon.components.modifier.Modifier} holds an integer delta and an
 * optional {@link ulb.models.bugemon.components.modifier.Ticker} that counts down remaining turns; the modifier is
 * considered expired when the ticker reaches zero. Permanent modifiers carry no ticker and never expire.
 */
package ulb.models.bugemon.components.modifier;
