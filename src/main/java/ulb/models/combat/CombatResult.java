/**
 * File name : CombatResult.java
 * Description : Record representing the result of a combat.
 *
 * @author OpenCode
 * @date 26 mar. 2026
 * @version 1.0
 */

package ulb.models.combat;

import java.util.Set;

import ulb.models.bugemon.Bugemon;
import ulb.models.trainer.Trainer;

// TODO: winnerParticipants & loserParticipants should probably be encapsulated inside Trainer.
public record CombatResult(Trainer winner, Trainer loser, Set<Bugemon> winnerParticipants,
                           Set<Bugemon> loserParticipants) {}
