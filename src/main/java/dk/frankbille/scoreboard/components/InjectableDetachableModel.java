package dk.frankbille.scoreboard.components;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.LoadableDetachableModel;

public abstract class InjectableDetachableModel<T> extends LoadableDetachableModel<T> {
	private static final long serialVersionUID = 1L;

	public InjectableDetachableModel() {
		Injector.get().inject(this);
	}

	public InjectableDetachableModel(T object) {
		super(object);
		Injector.get().inject(this);
	}
}
