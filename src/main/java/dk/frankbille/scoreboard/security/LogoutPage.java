package dk.frankbille.scoreboard.security;

import org.apache.wicket.Application;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebComponent;

import dk.frankbille.scoreboard.BasePage;
import dk.frankbille.scoreboard.ScoreBoardSession;
import dk.frankbille.scoreboard.components.menu.MenuItemType;

public class LogoutPage extends BasePage {
	private static final long serialVersionUID = 1L;

	public LogoutPage() {
		ScoreBoardSession.get().logout();
		
		WebComponent refresh = new WebComponent("refresh"); //$NON-NLS-1$
		StringBuilder content = new StringBuilder();
		content.append("1; url="); //$NON-NLS-1$
		content.append(getRequestCycle().urlFor(Application.get().getHomePage(), null));
		refresh.add(AttributeModifier.replace("content", content)); //$NON-NLS-1$
		add(refresh);
	}

	@Override
	public MenuItemType getMenuItemType() {
		return MenuItemType.SECURE;
	}

}
