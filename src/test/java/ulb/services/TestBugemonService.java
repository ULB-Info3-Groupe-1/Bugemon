package ulb.services;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ulb.models.bugemon.Bugemon;
import ulb.repositories.PlayerRepository;
import ulb.repositories.StaticDataRepository;
import ulb.repositories.dto.CreateBugemonDTO;
import ulb.repositories.exceptions.BugemonNameIsEmptyException;
import ulb.services.exceptions.BugemonNameAlreadyExistsException;
import ulb.utils.test.TestUtilsBugemons;

public class TestBugemonService {

    private static final String PLAYER = "Player1";
    private StaticDataRepository staticRepo;
    private PlayerRepository playerRepo;
    private BugemonService bugemonService;

    @Before
    public void setUp() {
        this.staticRepo = mock(StaticDataRepository.class);
        this.playerRepo = mock(PlayerRepository.class);

        when(this.staticRepo.getAllDefaultBugemons()).thenReturn(new ArrayList<>());
        this.bugemonService = new BugemonService(this.staticRepo, this.playerRepo, PLAYER);
    }

    @Test
    public void testSaveNewBugemon_EmptyNameThrowsException() {
        CreateBugemonDTO dto = new CreateBugemonDTO("", null, null, 0, 0, 0, 0, false, null, null, null);
        assertThrows(BugemonNameIsEmptyException.class, () -> this.bugemonService.saveNewBugemon(dto));
    }

    @Test
    public void testSaveNewBugemon_DuplicateNameThrowsException() {
        Bugemon bugemon = TestUtilsBugemons.createDefaultBugemon("Pikachu"); 
        List<Bugemon> cache = new ArrayList<>(List.of(bugemon));
        when(this.staticRepo.getAllDefaultBugemons()).thenReturn(cache);

        BugemonService serviceWithData = new BugemonService(this.staticRepo, this.playerRepo, PLAYER);
        CreateBugemonDTO dto = new CreateBugemonDTO("Pikachu", null, null, 10, 10, 10, 10, false, null, null, null);

        assertThrows(BugemonNameAlreadyExistsException.class, () -> serviceWithData.saveNewBugemon(dto));
    }

    @Test
    public void testDistributeXp_TriggersLevelUp() {
        Bugemon bugemon = mock(Bugemon.class);
        when(bugemon.gainXp(100)).thenReturn(1);

        this.bugemonService.distributeXp(bugemon, 100);

        assertTrue(this.bugemonService.hasPendingLevelUps());
        verify(bugemon).restoreHp();
        verify(this.playerRepo).updatePlayerBugemon(any());
    }

}
