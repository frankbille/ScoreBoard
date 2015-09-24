/*
 * ScoreBoard
 * Copyright (C) 2012-2013 Frank Bille
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
import dk.frankbille.scoreboard.components.menu.MenuItemType;
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

	private IModel<Player> playerModel;

	public PlayerPage(PageParameters parameters) {
		Long playerId = parameters.get(0).toLongObject();
		playerModel = new PlayerModel(playerId);

		final List<Game> playerGames = scoreBoardService.getPlayerGames(playerModel.getObject());
		Collections.sort(playerGames, new GameComparator());

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
				return playerGames;
			}
		};

		add(new PlayedGameListPanel("playedGameList", gamesModel, playerModel, null));
	}

	@Override
	public MenuItemType getMenuItemType() {
		if (ScoreBoardSession.get().isAuthenticated()) {
			Player player = playerModel.getObject();
			User user = ScoreBoardSession.get().getUser();
			Player userPlayer = user.getPlayer();
			if (userPlayer != null && userPlayer.equals(player)) {
				return MenuItemType.SECURE;
			}
		}
		
		return MenuItemType.PLAYERS;
	}

}
