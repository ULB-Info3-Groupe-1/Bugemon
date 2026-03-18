package ulb.models.bugemon;

import java.util.HashMap;
import java.util.Map;

public class Inventory {
    private final Map<GameObject, Integer> objects;

    public Inventory() {
        this.objects = new HashMap<>();
    }

    public Map<GameObject, Integer> getObjects() {
        return objects;
    }

    public void addObject(GameObject object, int quantity) {
        objects.put(object, objects.getOrDefault(object, 0) + quantity);
    }
}
