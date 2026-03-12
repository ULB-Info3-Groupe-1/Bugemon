package ulb.common.dto;

/**
 * Data transfer object interface for level-up choices.
 * <p>
 * Defines the contract for accessing stat bonuses that can be applied
 * when a Bugemon levels up and the player makes a choice.
 * </p>
 */
public interface ChoiceDTO {
    /**
     * Gets the bonus HP awarded by this choice.
     *
     * @return the HP bonus value
     */
    int getBonusHp();

    /**
     * Gets the bonus attack awarded by this choice.
     *
     * @return the attack bonus value
     */
    int getBonusAttack();

    /**
     * Gets the bonus defense awarded by this choice.
     *
     * @return the defense bonus value
     */
    int getBonusDefense();

    /**
     * Gets the bonus initiative awarded by this choice.
     *
     * @return the initiative bonus value
     */
    int getBonusInitiative();

    /**
     * Returns a string representation of this choice.
     *
     * @return a string describing this choice
     */
    String toString();
}
