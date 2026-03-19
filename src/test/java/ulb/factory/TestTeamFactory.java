package ulb.factory;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.github.cdimascio.dotenv.Dotenv;
import ulb.models.bugemon_team.BugemonTeam;
import ulb.repository.DatabaseRepository;

public class TestTeamFactory {
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    private static final String TEST_DB_URL = dotenv.get("TEST_DB_URL");

    @Test
    public void testRandomTeamNumber() {
        DatabaseRepository repository = new DatabaseRepository(TEST_DB_URL);
        BugemonTeam teamOfSix = TeamFactory.createRandomTeam(repository.getAllDefaultBugemons(), 6);
        assertEquals(6, teamOfSix.size());
    }
}
