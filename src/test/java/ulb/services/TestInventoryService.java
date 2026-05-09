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
        this.inventoryService = new InventoryService(this.playerRepo, this.inventoryRepo, PLAYER,
                mock(SkillService.class));
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
    public void resetInventory_shouldGrantStarterItems_whenSkillUnlocked() {
        SkillService skillService = mock(SkillService.class);
        Skill starterSkill = new SkillBuilder().effect(new StarterItemsEffect(2, "soin")).build();
        when(skillService.getSkills(StarterItemsEffect.class)).thenReturn(List.of(starterSkill));

        Item healingItem = new Item("potion", "Potion", "", ItemType.HEALING, null);
        Item boostItem = new Item("boost", "Boost", "", ItemType.BOOST, null);
        Inventory loaded = new Inventory();
        loaded.addItem(healingItem, 1);
        loaded.addItem(boostItem, 1);
        when(this.inventoryRepo.getPlayerInventory(PLAYER)).thenReturn(loaded);

        InventoryService service = new InventoryService(this.playerRepo, this.inventoryRepo, PLAYER, skillService);
        service.resetInventory();

        // healing item: 1 (default) + 2 (starter skill) = 3
        assertEquals(Integer.valueOf(3), service.getInventoryMap().get(healingItem));
        // boost item: untouched
        assertEquals(Integer.valueOf(1), service.getInventoryMap().get(boostItem));
    }

    @Test
    public void resetInventory_shouldNotChangeInventory_whenNoStarterItemsSkill() {
        SkillService skillService = mock(SkillService.class);
        when(skillService.getSkills(StarterItemsEffect.class)).thenReturn(List.of());

        Item healingItem = new Item("potion", "Potion", "", ItemType.HEALING, null);
        Inventory loaded = new Inventory();
        loaded.addItem(healingItem, 1);
        when(this.inventoryRepo.getPlayerInventory(PLAYER)).thenReturn(loaded);

        InventoryService service = new InventoryService(this.playerRepo, this.inventoryRepo, PLAYER, skillService);
        service.resetInventory();

        assertEquals(Integer.valueOf(1), service.getInventoryMap().get(healingItem));
    }
}
