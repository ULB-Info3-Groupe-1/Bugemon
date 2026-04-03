package ulb.models.bugemon;

public class AquaBugemon extends Bugemon {

    AquaBugemon() {
        super();
    }

    public AquaBugemon(Bugemon copy) {
        super(copy);
    }

    @Override
    public Bugemon clone() {
        return new AquaBugemon(this);
    }

    // TODO: remove this
    @Override
    public BugemonType getType() {
        return BugemonType.AQUA;
    }

}
