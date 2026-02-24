package models;

import com.google.gson.annotations.SerializedName;

import java.io.Serial;
import java.util.List;

public class Attack {

    private String id;

    @SerializedName("nom")
    private String name;

    private String type;

    private String description;

    @SerializedName("puissance")
    private int power;

    @SerializedName("effets")
    private List<Effect> effects;

    public Attack() {
    }

    public Attack(String id, String name, String type, String description, int power, List<Effect> effects) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.power = power;
        this.effects = effects;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPower() {
        return this.power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public List<Effect> getEffects() {
        return this.effects;
    }

    public void addEffect(Effect effect) {
        this.effects.add(effect);
    }
}