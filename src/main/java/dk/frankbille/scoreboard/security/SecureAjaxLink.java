package dk.frankbille.scoreboard.security;

import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.model.IModel;

public abstract class SecureAjaxLink<T> extends AjaxLink<T> implements RequiresLoginToRender {
	private static final long serialVersionUID = 1L;

	public SecureAjaxLink(String id) {
		super(id);
	}

	public SecureAjaxLink(String id, IModel<T> model) {
		super(id, model);
	}

}
