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

    public boolean isInInventory(String id) {
        return this.objects.stream()
                .anyMatch(objet -> objet.id().equals(id));
    }

    public GameObject getGameObject(String id) {
        return this.objects.stream()
                .filter(objet -> objet.id().equals(id)) // On garde uniquement l'objet avec le bon ID
                .findFirst()                            // On prend le premier trouvé
                .orElse(null);                          // Sécurité : si on ne le trouve pas, on renvoie "null" (rien)
    }
}