package dk.frankbille.scoreboard.daily;

import java.util.Collections;
import java.util.List;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import dk.frankbille.scoreboard.BasePage;
import dk.frankbille.scoreboard.ScoreBoardSession;
import dk.frankbille.scoreboard.comparators.GameComparator;
import dk.frankbille.scoreboard.components.GameStatisticsPanel;
import dk.frankbille.scoreboard.components.PlayedGameListPanel;
import dk.frankbille.scoreboard.components.PlayerStatisticsPanel;
import dk.frankbille.scoreboard.components.TooltipBehavior;
import dk.frankbille.scoreboard.components.TooltipBehavior.Placement;
import dk.frankbille.scoreboard.components.menu.MenuItemType;
import dk.frankbille.scoreboard.domain.Game;
import dk.frankbille.scoreboard.domain.League;
import dk.frankbille.scoreboard.domain.Player;
import dk.frankbille.scoreboard.domain.User;
import dk.frankbille.scoreboard.service.ScoreBoardService;


public class DailyGamePage extends BasePage {
	private static final long serialVersionUID = 1L;

	@SpringBean
	private ScoreBoardService scoreBoardService;

	private PlayedGameListPanel playedGameList;

	private PlayerStatisticsPanel playersContainer;

	private IModel<Player> loggedInPlayerModel;

	private League league;

	private WebMarkupContainer chartContainer;

    public DailyGamePage(final PageParameters parameters) {
    	long leagueId = parameters.get("league").toLong(-1);
    	if (leagueId < 1) {
    		goToDefaultLeague();
    	}

    	league = scoreBoardService.getLeague(leagueId);
    	if (league == null) {
    		goToDefaultLeague();
    	}

    	add(new Label("leagueName", league.getName()));

    	WebMarkupContainer chartToggle = new WebMarkupContainer("chartToggle");
    	chartToggle.add(AttributeAppender.replace("data-target", new AbstractReadOnlyModel<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getObject() {
				return "#"+chartContainer.getMarkupId();
			}
		}));
    	chartToggle.add(new TooltipBehavior(new Model<String>("Click to see graph"), Placement.RIGHT));
    	add(chartToggle);

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

    	addGameResults();

		addPlayerStatistics();
    }

	private void goToDefaultLeague() {
		long leagueId = 1;
		if (ScoreBoardSession.get().isAuthenticated()) {
			User user = ScoreBoardSession.get().getUser();
			League defaultLeague = user.getDefaultLeague();
			if (defaultLeague != null) {
				leagueId = defaultLeague.getId();
			}
		}
		PageParameters pp = new PageParameters();
		pp.add("league", leagueId);
		throw new RestartResponseException(DailyGamePage.class, pp);
	}

    @Override
    public MenuItemType getMenuItemType() {
    	return MenuItemType.DAILY;
    }

	private void addPlayerStatistics() {
		playersContainer = new PlayerStatisticsPanel("playersContainer", loggedInPlayerModel, league);
		playersContainer.setOutputMarkupId(true);
		add(playersContainer);
	}

	private void addGameResults() {
		IModel<List<Game>> gamesModel = new LoadableDetachableModel<List<Game>>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<Game> load() {
				List<Game> allGames = scoreBoardService.getAllGames(league);
				Collections.sort(allGames, new GameComparator());
				return allGames;
			}
		};

		chartContainer = new WebMarkupContainer("chartContainer");
		chartContainer.setOutputMarkupId(true);
		add(chartContainer);
		chartContainer.add(new GameStatisticsPanel("charts", gamesModel));

		playedGameList = new PlayedGameListPanel("playedGameList", gamesModel, loggedInPlayerModel);
		playedGameList.setOutputMarkupId(true);
		add(playedGameList);
	}
}
