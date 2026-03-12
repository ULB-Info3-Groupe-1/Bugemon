package ulb.models.bugemon;

import java.util.List;

// TODO: surely a better way to do this, but it works for now
public class ObjectWrapper {
    private List<GameObject> objects;
    private Inventory inventory;

    public ObjectWrapper(List<GameObject> objects, Inventory inventory) {
        this.objects = objects;
        this.inventory = inventory;
    }

    public List<GameObject> getObjects() {
        return objects;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setObjects(List<GameObject> objects) {
        this.objects = objects;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
}