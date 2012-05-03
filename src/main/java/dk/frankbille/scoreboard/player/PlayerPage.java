package dk.frankbille.scoreboard.player;

import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import dk.frankbille.scoreboard.BasePage;
import dk.frankbille.scoreboard.components.menu.MenuPanel.MenuItemType;
import dk.frankbille.scoreboard.domain.Player;
import dk.frankbille.scoreboard.service.ScoreBoardService;

public class PlayerPage extends BasePage {
	private static final long serialVersionUID = 1L;

	@SpringBean
	private ScoreBoardService scoreBoardService;

	public PlayerPage(PageParameters parameters) {
		Long playerId = parameters.get(0).toLongObject();
		IModel<Player> playerModel = new PlayerModel(playerId);

		PageParameters pp = new PageParameters();
		pp.set(0, playerId);
		add(new BookmarkablePageLink<Void>("editLink", PlayerEditPage.class, pp));
	}

	@Override
	public MenuItemType getMenuItemType() {
		return MenuItemType.PLAYERS;
	}

}
