package ulb.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ulb.models.bugemon.Inventory;
import ulb.models.bugemon.Item;
import ulb.models.bugemon.Item.ItemType;
import ulb.models.skills.Skill;
import ulb.models.skills.SkillBuilder;
import ulb.models.skills.SkillEffect.StarterItemsEffect;
import ulb.repositories.InventoryRepository;

public class TestInventoryService {

    private static final String PLAYER = "Player1";
    private InventoryRepository inventoryRepo;
    private InventoryService inventoryService;

    @Before
    public void setUp() {
        this.inventoryRepo = mock(InventoryRepository.class);
        this.inventoryService = new InventoryService(PLAYER, this.inventoryRepo, mock(SkillService.class));

        Item item = mock(Item.class);
        Inventory inventory = new Inventory();
        inventory.addItem(item, 1);
        when(this.inventoryRepo.getPlayerInventory(PLAYER)).thenReturn(new Inventory());
    }

    @Test
    public void testLoadInventory() {
        Inventory mockInventory = mock(Inventory.class);
        when(this.inventoryRepo.getPlayerInventory(PLAYER)).thenReturn(mockInventory);

        assertEquals(mockInventory, this.inventoryService.loadInventory());
        verify(this.inventoryRepo).getPlayerInventory(PLAYER);
    }

    @Test
    public void testResetInventory() {
        this.inventoryService.resetInventory();
        verify(this.inventoryRepo).addDefaultInventory(PLAYER);
    }

    @Test
    public void loadInventory_shouldGrantStarterItems_whenSkillUnlocked() {
        SkillService skillService = mock(SkillService.class);
        Skill starterSkill = new SkillBuilder().effect(new StarterItemsEffect(2, "soin")).build();
        when(skillService.getSkills(StarterItemsEffect.class)).thenReturn(List.of(starterSkill));

        Item healingItem = new Item("potion", "Potion", "", ItemType.HEALING, null);
        Item boostItem = new Item("boost", "Boost", "", ItemType.BOOST, null);
        Inventory loadedInventoryMock = new Inventory();
        loadedInventoryMock.addItem(healingItem, 1);
        loadedInventoryMock.addItem(boostItem, 1);

        when(this.inventoryRepo.getPlayerInventory(PLAYER)).thenReturn(loadedInventoryMock);

        InventoryService service = new InventoryService(PLAYER, this.inventoryRepo, skillService);
        Inventory resultInventory = service.loadInventory();

        assertEquals(Integer.valueOf(3), resultInventory.getMap().get(healingItem));
        assertEquals(Integer.valueOf(1), resultInventory.getMap().get(boostItem));
    }

    @Test
    public void loadInventory_shouldNotChangeInventory_whenNoStarterItemsSkill() {
        SkillService skillService = mock(SkillService.class);
        when(skillService.getSkills(StarterItemsEffect.class)).thenReturn(List.of());

        Item healingItem = new Item("potion", "Potion", "", ItemType.HEALING, null);
        Inventory loadedInventoryMock = new Inventory();
        loadedInventoryMock.addItem(healingItem, 1);

        when(this.inventoryRepo.getPlayerInventory(PLAYER)).thenReturn(loadedInventoryMock);

        InventoryService service = new InventoryService(PLAYER, this.inventoryRepo, skillService);
        Inventory resultInventory = service.loadInventory();

        assertEquals(Integer.valueOf(1), resultInventory.getMap().get(healingItem));
    }
}
