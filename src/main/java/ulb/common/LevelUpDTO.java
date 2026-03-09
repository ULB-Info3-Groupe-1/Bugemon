/**
 * File name : LevelUpDTO.java
 * Description : Interface defining the data transfer object for the level-up process of a Bugemon
 * @author Gouverneur Martin
 * @co-author Verbeiren Lucas
 * @date 09 mar. 2026
 * @version 1.0
 */
package ulb.common;
import java.util.List;

import ulb.models.level_up.Choice;

public interface LevelUpDTO {
    BugemonDTO getBugemon();

    List<Choice> getChoices();
}
