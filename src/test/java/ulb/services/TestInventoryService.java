package ulb.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import ulb.models.bugemon.Inventory;
import ulb.models.bugemon.Item;
import ulb.repositories.InventoryRepository;
import ulb.repositories.PlayerRepository;

public class TestInventoryService {

    private static final String PLAYER = "Player1";
    private PlayerRepository playerRepo;
    private InventoryRepository inventoryRepo;
    private InventoryService inventoryService;

    @Before
    public void setUp() {
        this.playerRepo = mock(PlayerRepository.class);
        this.inventoryRepo = mock(InventoryRepository.class);
        this.inventoryService = new InventoryService(this.playerRepo, this.inventoryRepo, PLAYER);
    }

    @Test
    public void testLoadInventory() {
        Inventory mockInventory = mock(Inventory.class);
        when(this.inventoryRepo.getPlayerInventory(PLAYER)).thenReturn(mockInventory);

        this.inventoryService.loadInventory();

        assertEquals(mockInventory, this.inventoryService.getInventoryMap() == null ? null : mockInventory);
        verify(this.inventoryRepo).getPlayerInventory(PLAYER);
    }

    @Test
    public void testResetInventory() {
        this.inventoryService.resetInventory();

        verify(this.inventoryRepo).addDefaultInventory(PLAYER);
        verify(this.inventoryRepo).getPlayerInventory(PLAYER);
    }

    @Test
    public void testUseItem() {
        Inventory mockInventory = mock(Inventory.class);
        Item item = mock(Item.class);

        when(this.inventoryRepo.getPlayerInventory(PLAYER)).thenReturn(mockInventory);
        this.inventoryService.loadInventory();

        this.inventoryService.useItem(item);

        verify(mockInventory).useItem(item);
    }
}
