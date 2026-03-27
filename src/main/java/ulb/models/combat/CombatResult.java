/**
 * File name : CombatResult.java
 * Description : Record representing the result of a combat.
 *
 * @author OpenCode
 * @date 26 mar. 2026
 * @version 1.0
 */

package ulb.models.combat;

import ulb.models.trainer.Trainer;

public record CombatResult(Trainer winner, Trainer loser) {}
