package ulb.common;

import ulb.models.bugemon.Bugemon;

public interface BugemonDTO {

    /**
     * Returns the URL of the sprite image associated with the Bugemon.
     * @return (String) the URL of the sprite image as a String
     */
    String getSpriteURL();

    /**
     * Returns the unique identifier of the Bugemon.
     * @return (String) the unique identifier of the Bugemon as a String
     */
    String getId();

    /**
     * Returns the name of the Bugemon.
     * @return (String) the name of the Bugemon as a String
     */
    String getName();

    /**
     * Returns the type of the Bugemon.
     * @return (Bugemon.BType) the type of the Bugemon as an enum value
     */
    Bugemon.BType getType(); //TODO: move enum outside Models

    /**
     * Returns the current health points (HP) of the Bugemon.
     * @return (int) the current HP of the Bugemon as an integer
     */
    int getHp();

}
