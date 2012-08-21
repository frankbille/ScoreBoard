package dk.frankbille.scoreboard.security;

import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.model.IModel;

public abstract class SecureRenderingAjaxLink<T> extends AjaxLink<T> implements RequiresLoginToRender {
	private static final long serialVersionUID = 1L;

	public SecureRenderingAjaxLink(String id) {
		super(id);
	}

	public SecureRenderingAjaxLink(String id, IModel<T> model) {
		super(id, model);
	}

}
