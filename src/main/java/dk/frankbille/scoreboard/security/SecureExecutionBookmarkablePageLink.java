package dk.frankbille.scoreboard.security;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class SecureExecutionBookmarkablePageLink<T> extends BookmarkablePageLink<T> implements RequiresLoginToBeEnabled {
	private static final long serialVersionUID = 1L;

	public <C extends Page> SecureExecutionBookmarkablePageLink(String id, Class<C> pageClass) {
		super(id, pageClass);
	}

	public <C extends Page> SecureExecutionBookmarkablePageLink(String id, Class<C> pageClass, PageParameters parameters) {
		super(id, pageClass, parameters);
	}

}
