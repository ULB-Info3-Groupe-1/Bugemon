package ulb.factory;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ulb.models.bugemon_team.BugemonTeam;
import ulb.repository.DatabaseRepository;

public class TestTeamFactory {
    @Test
    public void testRandomTeamNumber() {
        BugemonTeam teamOfSix = TeamFactory.createRandomTeam(
                DatabaseRepository.getInstance().getAllDefaultBugemons(), 6);
        assertEquals(6, teamOfSix.size());
    }
}
