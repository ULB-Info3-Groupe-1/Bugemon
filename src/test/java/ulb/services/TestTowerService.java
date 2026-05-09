package ulb.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import ulb.Configuration;
import ulb.repositories.PlayerRepository;

public class TestTowerService {

    private static final String PLAYER = "Player1";
    private PlayerRepository playerRepo;
    private TowerService towerService;

    @Before
    public void setUp() {
        this.playerRepo = mock(PlayerRepository.class);
        this.towerService = new TowerService(this.playerRepo, PLAYER, null, null, null);
    }

    @Test
    public void testLoadTowerProgress() {
        when(this.playerRepo.getPlayerCurrentFloor(PLAYER)).thenReturn(5);
        assertEquals(5, this.towerService.getCurrentFloor());
    }

    @Test
    public void testClearTowerProgress() {
        when(this.playerRepo.getPlayerCurrentFloor(PLAYER)).thenReturn(Configuration.Game.FLOOR_MIN);
        this.towerService.clearTowerProgress();
        assertEquals(Configuration.Game.FLOOR_MIN, this.towerService.getCurrentFloor());
        verify(this.playerRepo).setPlayerCurrentFloor(PLAYER, Configuration.Game.FLOOR_MIN);
    }
}
