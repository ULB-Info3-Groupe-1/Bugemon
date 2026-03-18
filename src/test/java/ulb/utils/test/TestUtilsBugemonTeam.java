package ulb.utils.test;

import java.util.ArrayList;
import java.util.List;

import ulb.models.bugemon.Bugemon;

public class TestUtilsBugemonTeam {
    private TestUtilsBugemonTeam() {}

    public static List<Bugemon> createDefaultBugemonTeam(boolean isDefeated) {
        List<Bugemon> bugemonTeam = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            bugemonTeam.add(TestUtilsBugemons.createDefaultBugemon(String.valueOf(i)));
        }
        if (isDefeated) {
            for (Bugemon bugemon : bugemonTeam) {
                bugemon.takeDamage(bugemon.getHp());
            }
        }
        return bugemonTeam;
    }
}
