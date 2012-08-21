package dk.frankbille.scoreboard.player;

import java.util.Collections;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import dk.frankbille.scoreboard.BasePage;
import dk.frankbille.scoreboard.ScoreBoardSession;
import dk.frankbille.scoreboard.comparators.GameComparator;
import dk.frankbille.scoreboard.components.PlayedGameListPanel;
import dk.frankbille.scoreboard.components.menu.MenuPanel.MenuItemType;
import dk.frankbille.scoreboard.domain.Game;
import dk.frankbille.scoreboard.domain.Player;
import dk.frankbille.scoreboard.domain.User;
import dk.frankbille.scoreboard.security.SecureRenderingBookmarkablePageLink;
import dk.frankbille.scoreboard.security.SecureRenderingLink;
import dk.frankbille.scoreboard.service.ScoreBoardService;

public class PlayerPage extends BasePage {
	private static final long serialVersionUID = 1L;

	@SpringBean
	private ScoreBoardService scoreBoardService;

	public PlayerPage(PageParameters parameters) {
		Long playerId = parameters.get(0).toLongObject();
		final IModel<Player> playerModel = new PlayerModel(playerId);

		// Name
		add(new Label("name", new PropertyModel<String>(playerModel, "name")));

		// Edit link
		PageParameters pp = new PageParameters();
		pp.set(0, playerId);
		add(new SecureRenderingBookmarkablePageLink<Void>("editLink", PlayerEditPage.class, pp));

		add(new SecureRenderingLink<Void>("claimLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				User user = ScoreBoardSession.get().getUser();
				user.setPlayer(playerModel.getObject());
				scoreBoardService.updateUser(user);
			}

			@Override
			public boolean isVisible() {
				User user = ScoreBoardSession.get().getUser();
				return user != null && user.getPlayer() == null && scoreBoardService.getUserForPlayer(playerModel.getObject()) == null;
			}
		});

		// Played game list
		IModel<List<Game>> gamesModel = new LoadableDetachableModel<List<Game>>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<Game> load() {
				List<Game> playerGames = scoreBoardService.getPlayerGames(playerModel.getObject());
				Collections.sort(playerGames, new GameComparator());
				return playerGames;
			}
		};

		add(new PlayedGameListPanel("playedGameList", gamesModel, playerModel));
	}

	@Override
	public MenuItemType getMenuItemType() {
		return MenuItemType.PLAYERS;
	}

}
