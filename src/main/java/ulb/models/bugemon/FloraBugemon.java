package ulb.models.bugemon;

public class FloraBugemon extends Bugemon {

    FloraBugemon() {
        super();
    }

    public FloraBugemon(Bugemon copy) {
        super(copy);
    }

    @Override
    public Bugemon clone() {
        return new FloraBugemon(this);
    }

    // TODO: remove this
    @Override
    public BugemonType getType() {
        return BugemonType.FLORA;
    }

}
