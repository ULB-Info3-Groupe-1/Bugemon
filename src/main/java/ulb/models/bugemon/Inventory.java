package ulb.models.bugemon;

import java.util.List;

public class Inventory {
    private List<GameObject> objects;

    public Inventory(List<GameObject> objects) {
        this.objects = objects;
    }

    public List<GameObject> getObjects() {
        return objects;
    }

    public void addObject(GameObject object, int quantity) {
        for (int i = 0; i < quantity; i++) {
            objects.add(object);
        }
    }
}