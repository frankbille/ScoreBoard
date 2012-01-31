package dk.frankbille.scoreboard.player;

import org.apache.wicket.request.mapper.parameter.PageParameters;

import dk.frankbille.scoreboard.BasePage;
import dk.frankbille.scoreboard.components.menu.MenuPanel.MenuItemType;

public class PlayerPage extends BasePage {
	private static final long serialVersionUID = 1L;

	public PlayerPage(PageParameters parameters) {
	}

	@Override
	public MenuItemType getMenuItemType() {
		return MenuItemType.PLAYERS;
	}

}
