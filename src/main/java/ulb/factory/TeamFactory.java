package ulb.factory;

import java.util.List;
import java.util.Random;

import ulb.models.bugemon.Bugemon;
import ulb.models.bugemon_team.BugemonTeam;

public class TeamFactory {
    private static final Random RANDOM = new Random();

    /**
     * Generates a random bugemon team of 6 bugemons
     * @return the bugemon list created
     */
    public static BugemonTeam createRandomTeam(final List<Bugemon> bugemonList,
                                               final int teamSize) {
        BugemonTeam randomTeam = new BugemonTeam();

        while (randomTeam.size() != teamSize) {
            int randomIndex = RANDOM.nextInt(bugemonList.size());
            Bugemon chosenBugemon = bugemonList.get(randomIndex);

            if (!randomTeam.contains(chosenBugemon.getId())) {
                try {
                    randomTeam.addBugemon(chosenBugemon.clone());
                } catch (CloneNotSupportedException e) {
                    throw new RuntimeException("Failed to clone Bugemon: " + chosenBugemon.getId(),
                                               e);
                }
            }
        }
        return randomTeam;
    }
}
