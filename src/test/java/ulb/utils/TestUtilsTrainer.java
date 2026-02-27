package ulb.utils;

import ulb.models.trainer.Trainer;

public class TestUtilsTrainer {

    private TestUtilsTrainer() {}

    public static Trainer createDefaultTrainer() {
        return new Trainer(TestUtilsBugemonTeam.createDefaultBugemonTeam());
    }
}
