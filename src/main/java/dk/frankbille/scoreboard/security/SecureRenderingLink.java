package dk.frankbille.scoreboard.security;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;

public abstract class SecureRenderingLink<T> extends Link<T> implements RequiresLoginToRender {
	private static final long serialVersionUID = 1L;

	public SecureRenderingLink(String id) {
		super(id);
	}

	public SecureRenderingLink(String id, IModel<T> model) {
		super(id, model);
	}

}
