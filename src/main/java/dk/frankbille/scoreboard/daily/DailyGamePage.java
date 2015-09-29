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

package dk.frankbille.scoreboard.daily;

import java.util.Collections;
import java.util.List;

import dk.frankbille.scoreboard.ratings.RatingCalculator;
import dk.frankbille.scoreboard.ratings.RatingProvider;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import dk.frankbille.scoreboard.BasePage;
import dk.frankbille.scoreboard.ScoreBoardSession;
import dk.frankbille.scoreboard.comparators.GameComparator;
import dk.frankbille.scoreboard.components.GameStatisticsPanel;
import dk.frankbille.scoreboard.components.PlayedGameListPanel;
import dk.frankbille.scoreboard.components.PlayerStatisticsPanel;
import dk.frankbille.scoreboard.components.TeamStatisticsPanel;
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

	private TeamStatisticsPanel teamsContainer;

	private IModel<Player> loggedInPlayerModel;

	private League league;

	private WebMarkupContainer chartContainer;
	private final RatingCalculator ratings;
	private final List<Game> games;

	public DailyGamePage(final PageParameters parameters) {
    	long leagueId = parameters.get("league").toLong(-1);
    	if (leagueId < 1) {
    		goToDefaultLeague();
    	}

    	league = scoreBoardService.getLeague(leagueId);
    	if (league == null) {
    		goToDefaultLeague();
    	}

		games = scoreBoardService.getAllGames(league);
		ratings = RatingProvider.getRatings(league, games);
		Collections.sort(games, new GameComparator());

    	add(new Label("leagueName", league.getName()));

    	WebMarkupContainer chartToggle = new WebMarkupContainer("chartToggle");
    	chartToggle.add(AttributeAppender.replace("data-target", new AbstractReadOnlyModel<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getObject() {
				return "#"+chartContainer.getMarkupId();
			}
		}));
    	chartToggle.add(new TooltipBehavior(new StringResourceModel("clickToSeeChart", null), Placement.RIGHT));
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
		addTeamsStatistics();
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
		playersContainer = new PlayerStatisticsPanel("playersContainer", loggedInPlayerModel, league, ratings);
		playersContainer.setOutputMarkupId(true);
		add(playersContainer);
	}

	private void addTeamsStatistics() {
		teamsContainer = new TeamStatisticsPanel("teamsContainer", league, ratings);
		teamsContainer.setOutputMarkupId(true);
		add(teamsContainer);
	}

	private void addGameResults() {
		IModel<List<Game>> gamesModel = new LoadableDetachableModel<List<Game>>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<Game> load() {
				return games;
			}
		};

		chartContainer = new WebMarkupContainer("chartContainer");
		chartContainer.setOutputMarkupId(true);
		add(chartContainer);
		chartContainer.add(new GameStatisticsPanel("charts", gamesModel, ratings));

		playedGameList = new PlayedGameListPanel("playedGameList", gamesModel, loggedInPlayerModel, ratings);
		playedGameList.setOutputMarkupId(true);
		add(playedGameList);
	}
}
