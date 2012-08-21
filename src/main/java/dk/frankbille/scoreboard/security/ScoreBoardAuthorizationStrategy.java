package dk.frankbille.scoreboard.security;

import org.apache.wicket.Component;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authorization.strategies.page.SimplePageAuthorizationStrategy;

import dk.frankbille.scoreboard.BasePage;
import dk.frankbille.scoreboard.ScoreBoardSession;

public class ScoreBoardAuthorizationStrategy extends SimplePageAuthorizationStrategy {

	public ScoreBoardAuthorizationStrategy() {
		super(SecureBasePage.class, BasePage.class);
	}

	@Override
	public boolean isActionAuthorized(Component component, Action action) {
		if (component instanceof RequiresLoginToRender && Component.RENDER.equals(action)) {
			return isAuthorized();
		} else if (component instanceof RequiresLoginToBeEnabled && Component.ENABLE.equals(action)) {
			return isAuthorized();
		}
		return true;
	}

	@Override
	protected boolean isAuthorized() {
		return ScoreBoardSession.get().isAuthenticated();
	}

}
