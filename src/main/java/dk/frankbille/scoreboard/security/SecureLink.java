package dk.frankbille.scoreboard.security;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;

public abstract class SecureLink<T> extends Link<T> implements RequiresLoginToRender {
	private static final long serialVersionUID = 1L;

	public SecureLink(String id) {
		super(id);
	}

	public SecureLink(String id, IModel<T> model) {
		super(id, model);
	}

}
