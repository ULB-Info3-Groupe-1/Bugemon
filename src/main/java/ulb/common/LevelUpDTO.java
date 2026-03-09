package ulb.common;
import java.util.List;

import ulb.models.level_up.Choice;

public interface LevelUpDTO {

    BugemonDTO getBugemon();

    List<Choice> getChoices();
}
