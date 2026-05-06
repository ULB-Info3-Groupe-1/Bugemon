package ulb.models.tower;

import java.util.ArrayList;
import java.util.Collection;

import ulb.models.tower.exceptions.FloorLevelAlreadyExistsException;
import ulb.models.tower.exceptions.FloorLevelNotFoundException;

/**
 * A list of {@link Floor}s. It enforces unique floor levels, ensuring that a specific level (such as NO2 or NO3) cannot
 * appear more than once in the tower.
 */
public class TowerFloors extends ArrayList<Floor> {

    private void ensureUniqueFloorLevel(int newFloorLevel) {
        if (this.stream().anyMatch(f -> f.getFloorLevel() == newFloorLevel)) {
            throw new FloorLevelAlreadyExistsException(newFloorLevel);
        }
    }

    @Override
    public boolean add(Floor newFloor) {
        this.ensureUniqueFloorLevel(newFloor.getFloorLevel());
        return super.add(newFloor);
    }

    @Override
    public void add(int index, Floor newFloor) {
        this.ensureUniqueFloorLevel(newFloor.getFloorLevel());
        super.add(index, newFloor);
    }

    @Override
    public boolean addAll(Collection<? extends Floor> c) {
        c.forEach(this::add);
        return true;
    }

    @Override
    public Floor set(int index, Floor element) {
        if (this.get(index).getFloorLevel() != element.getFloorLevel()) {
            this.ensureUniqueFloorLevel(element.getFloorLevel());
        }
        return super.set(index, element);
    }

    /**
     * Returns the floor with the given level.
     *
     * @param level
     *            floor level
     * @return (Floor) the floor with the given level
     */
    public Floor getFloorByLevel(int level) {
        return this.stream().filter(floor -> floor.getFloorLevel() == level).findFirst()
                .orElseThrow(() -> new FloorLevelNotFoundException(level));
    }
}
