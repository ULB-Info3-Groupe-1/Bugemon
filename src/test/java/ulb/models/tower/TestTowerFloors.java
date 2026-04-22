package ulb.models.tower;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ulb.models.tower.exceptions.FloorLevelAlreadyExistsException;
import ulb.models.tower.exceptions.FloorLevelNotFoundException;

public class TestTowerFloors {

    private TowerFloors towerFloors;
    private Floor floorLevel2;
    private Floor floorLevel3;
    private Floor floorLevel4;

    @Before
    public void setUp() {
        this.towerFloors = new TowerFloors();

        this.floorLevel2 = mock(Floor.class);
        when(this.floorLevel2.getFloorLevel()).thenReturn(2);

        this.floorLevel3 = mock(Floor.class);
        when(this.floorLevel3.getFloorLevel()).thenReturn(3);

        this.floorLevel4 = mock(Floor.class);
        when(this.floorLevel4.getFloorLevel()).thenReturn(4);
    }

    @Test
    public void testAddUniqueFloors() {
        assertTrue(this.towerFloors.add(this.floorLevel2));
        assertTrue(this.towerFloors.add(this.floorLevel3));
        assertEquals(2, this.towerFloors.size());
    }

    @Test
    public void testAddDuplicateFloorThrowsException() {
        this.towerFloors.add(this.floorLevel2);
        assertThrows(FloorLevelAlreadyExistsException.class, () -> this.towerFloors.add(this.floorLevel2));
    }

    @Test
    public void testAddAtIndexDuplicateThrowsException() {
        this.towerFloors.add(this.floorLevel2);
        assertThrows(FloorLevelAlreadyExistsException.class, () -> this.towerFloors.add(0, this.floorLevel2));
    }

    @Test
    public void testAddAllWithDuplicatesThrowsException() {
        this.towerFloors.add(this.floorLevel2);
        List<Floor> newFloors = Arrays.asList(this.floorLevel3, this.floorLevel2);
        assertThrows(FloorLevelAlreadyExistsException.class, () -> this.towerFloors.addAll(newFloors));
    }

    @Test
    public void testSetFloorWithSameLevelIsAllowed() {
        this.towerFloors.add(this.floorLevel2);
        Floor anotherFloorLevel2 = mock(Floor.class);
        when(anotherFloorLevel2.getFloorLevel()).thenReturn(2);

        this.towerFloors.set(0, anotherFloorLevel2);
        assertEquals(2, this.towerFloors.get(0).getFloorLevel());
    }

    @Test
    public void testSetFloorWithDuplicateLevelThrowsException() {
        this.towerFloors.add(this.floorLevel2);
        this.towerFloors.add(this.floorLevel3);
        assertThrows(FloorLevelAlreadyExistsException.class, () -> this.towerFloors.set(0, this.floorLevel3));
    }

    @Test
    public void testGetFloorByLevelSuccess() {
        this.towerFloors.add(this.floorLevel2);
        this.towerFloors.add(this.floorLevel3);

        Floor found = this.towerFloors.getFloorByLevel(3);
        assertEquals(3, found.getFloorLevel());
        assertEquals(this.floorLevel3, found);
    }

    @Test
    public void testGetFloorByLevelNotFoundThrowsException() {
        this.towerFloors.add(this.floorLevel2);
        assertThrows(FloorLevelNotFoundException.class, () -> this.towerFloors.getFloorByLevel(99));
    }
}
