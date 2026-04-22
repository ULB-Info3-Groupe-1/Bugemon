package ulb.utils.test;

import ulb.models.trainer.AutoTrainer;

public class TestUtilsTrainer {
    private TestUtilsTrainer() {
    }

    public static AutoTrainer createDefaultAutoTrainer() {
        return new AutoTrainer(TestUtilsBugemonTeam.createDefaultBugemonTeam(false), null, null);
    }
}
