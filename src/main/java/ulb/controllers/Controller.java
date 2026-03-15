package ulb.controllers;

public abstract class Controller {

	protected final MetaController metaController;

	public Controller(MetaController metaController) {
	    this.metaController = metaController;
	}

	public abstract String alert();
}
