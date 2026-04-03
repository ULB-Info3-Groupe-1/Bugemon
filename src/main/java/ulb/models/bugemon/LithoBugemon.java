package ulb.models.bugemon;

public class LithoBugemon extends Bugemon {

    LithoBugemon() {
        super();
    }

    public LithoBugemon(Bugemon copy) {
        super(copy);
    }

    @Override
    public Bugemon clone() {
        return new LithoBugemon(this);
    }

    // TODO: remove this
    @Override
    public BugemonType getType() {
        return BugemonType.LITHO;
    }

}
