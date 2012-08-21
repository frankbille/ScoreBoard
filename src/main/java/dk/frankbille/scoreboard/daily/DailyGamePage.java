package dk.frankbille.scoreboard.daily;

import java.util.Collections;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import dk.frankbille.scoreboard.BasePage;
import dk.frankbille.scoreboard.ScoreBoardSession;
import dk.frankbille.scoreboard.comparators.GameComparator;
import dk.frankbille.scoreboard.components.PlayedGameListPanel;
import dk.frankbille.scoreboard.components.PlayedGameListPanel.GameSelectedCallback;
import dk.frankbille.scoreboard.components.PlayerStatisticsPanel;
import dk.frankbille.scoreboard.components.menu.MenuPanel.MenuItemType;
import dk.frankbille.scoreboard.domain.Game;
import dk.frankbille.scoreboard.domain.Player;
import dk.frankbille.scoreboard.service.ScoreBoardService;


public class DailyGamePage extends BasePage {
	private static final long serialVersionUID = 1L;

	@SpringBean
	private ScoreBoardService scoreBoardService;

	private PlayedGameListPanel playedGameList;

	private PlayerStatisticsPanel playersContainer;

	private IModel<Player> loggedInPlayerModel;

    public DailyGamePage(final PageParameters parameters) {
		loggedInPlayerModel = new LoadableDetachableModel<Player>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected Player load() {
				ScoreBoardSession scoreBoardSession = ScoreBoardSession.get();
				if (scoreBoardSession.isAuthenticated()) {
					return scoreBoardSession.getUser().getPlayer();
				}
				return null;
			}
		};

    	addNewGame();

    	addGameResults();

		addPlayerStatistics();
    }

    @Override
    public MenuItemType getMenuItemType() {
    	return MenuItemType.DAILY;
    }

	private void addPlayerStatistics() {
		playersContainer = new PlayerStatisticsPanel("playersContainer", loggedInPlayerModel);
		playersContainer.setOutputMarkupId(true);
		add(playersContainer);
	}

	private void addNewGame() {
		addGame(null, null);
	}
	
	private void addGame(Long gameId, AjaxRequestTarget target) {
		EditGamePanel editGamePanel = new EditGamePanel("editGame", gameId) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void newGameAdded(Game game, AjaxRequestTarget target) {
				target.add(playedGameList);
				target.add(playersContainer);
			}
		};
		editGamePanel.setOutputMarkupId(true);
		addOrReplace(editGamePanel);
		if (target != null) {
			target.add(editGamePanel);
		}
	}

	private void addGameResults() {
		IModel<List<Game>> gamesModel = new LoadableDetachableModel<List<Game>>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<Game> load() {
				List<Game> allGames = scoreBoardService.getAllGames();
				Collections.sort(allGames, new GameComparator());
				return allGames;
			}
		};

		playedGameList = new PlayedGameListPanel("playedGameList", gamesModel, loggedInPlayerModel, new GameSelectedCallback() {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSelection(AjaxRequestTarget target, Game game) {
				addGame(game.getId(), target);
			}
		});
		playedGameList.setOutputMarkupId(true);
		add(playedGameList);
	}
}
