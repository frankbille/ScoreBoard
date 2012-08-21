package dk.frankbille.scoreboard.security;

import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.model.IModel;

public abstract class SecureExecutionAjaxLink<T> extends AjaxLink<T> implements RequiresLoginToBeEnabled {
	private static final long serialVersionUID = 1L;

	public SecureExecutionAjaxLink(String id) {
		super(id);
	}

	public SecureExecutionAjaxLink(String id, IModel<T> model) {
		super(id, model);
	}

}
