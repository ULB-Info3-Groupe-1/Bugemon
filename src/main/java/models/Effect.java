package models;

import com.google.gson.annotations.SerializedName;

public class Effect {

    private String type;

    @SerializedName("cible")
    private String target;

    private String stat;

    @SerializedName("modificateur")
    private int modifier;
    
    private String duration;

    public Effect() {
    }

    public Effect(String type, String target, String stat, int modifier, String duration) {
        this.type = type;
        this.target = target;
        this.stat = stat;
        this.modifier = modifier;
        this.duration = duration;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTarget() {
        return this.target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getStat() {
        return this.stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public int getModifier() {
        return this.modifier;
    }

    public void setmModifier(int modifier) {
        this.modifier = modifier;
    }

    public String getDuration() {
        return this.duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}