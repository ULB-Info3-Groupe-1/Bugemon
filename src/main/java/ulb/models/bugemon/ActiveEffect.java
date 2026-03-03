/**
 * File name : ActiveEffect.java
 * Description : Wrapper for the Effect class to handle duration.
 *
 * @author Rocca Manuel
 * @date 3 mar. 2026
 * @version 1.0
 */

package ulb.models.bugemon;


/**
 * 
 */
public class ActiveEffect {

    private final Effect effect;
    private int durationLeft; // -1 = infinite


    /**
     * 
     * @param effect
     * @param durationLeft
     */
    public ActiveEffect(Effect effect, int durationLeft) {
        this.effect = effect;
        this.durationLeft = durationLeft;
    }

    /**
     * 
     * @return
     */
    public Effect getEffect() {
        return this.effect;
    }

    /**
     * 
     */
    public void decrementDuration() {
        if (this.durationLeft > 0) {
            this.durationLeft--;
        }
    }

    /**
     * 
     * @return
     */
    public int getDuration() {
        return this.durationLeft;
    }

    /**
     * 
     * @return
     */
    public boolean isExpired() {
        return this.durationLeft == 0;
    }

}
