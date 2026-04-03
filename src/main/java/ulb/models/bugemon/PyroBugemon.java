
package ulb.models.bugemon;

public class PyroBugemon extends Bugemon {

    PyroBugemon() {
        super();
    }

    public PyroBugemon(Bugemon copy) {
        super(copy);
    }

    @Override
    public Bugemon clone() {
        return new PyroBugemon(this);
    }

    // TODO: remove this
    @Override
    public BugemonType getType() {
        return BugemonType.PYRO;
    }

}
