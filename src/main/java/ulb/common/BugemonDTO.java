package ulb.common;

import ulb.models.bugemon.Bugemon;

public interface BugemonDTO {

    String getSpriteURL();

    String getId();

    String getName();

    Bugemon.BType getType(); //TODO: move enum outside Models

    int getHp();

}
