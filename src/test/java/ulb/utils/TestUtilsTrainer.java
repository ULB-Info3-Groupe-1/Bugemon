package ulb.utils;

import ulb.models.trainer.AutoTrainer;
import ulb.models.trainer.Trainer;

public class TestUtilsTrainer {
    private TestUtilsTrainer() {}

    public static Trainer createDefaultTrainer() {
        return new Trainer(TestUtilsBugemonTeam.createDefaultBugemonTeam(false));
    }

    public static AutoTrainer createDefaultAutoTrainer() {
        return new AutoTrainer(TestUtilsBugemonTeam.createDefaultBugemonTeam(false));
    }
}
