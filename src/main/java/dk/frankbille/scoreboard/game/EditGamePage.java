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

package dk.frankbille.scoreboard.game;

import java.util.Date;
import java.util.List;

import dk.frankbille.scoreboard.components.LeagueSelector;
import org.apache.wicket.Application;
import org.apache.wicket.Localizer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import dk.frankbille.scoreboard.ScoreBoardSession;
import dk.frankbille.scoreboard.components.DateField;
import dk.frankbille.scoreboard.components.Select2Enabler;
import dk.frankbille.scoreboard.components.menu.MenuItemType;
import dk.frankbille.scoreboard.daily.DailyGamePage;
import dk.frankbille.scoreboard.domain.Game;
import dk.frankbille.scoreboard.domain.GameTeam;
import dk.frankbille.scoreboard.domain.League;
import dk.frankbille.scoreboard.domain.Team;
import dk.frankbille.scoreboard.domain.User;
import dk.frankbille.scoreboard.security.SecureBasePage;
import dk.frankbille.scoreboard.service.ScoreBoardService;

public class EditGamePage extends SecureBasePage {
	private static final long serialVersionUID = 1L;

	@SpringBean
	private ScoreBoardService scoreBoardService;

	private Game game;
	
	public EditGamePage(PageParameters pageParameters) {
		long gameId = pageParameters.get(0).toLong(-1);
		
		if (gameId > 0) {
			game = scoreBoardService.getGame(gameId);
		} else {
			game = createNewGame();
		}

		add(new Label("editGameTitle", new AbstractReadOnlyModel<String>() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public String getObject() {
				Localizer localizer = Application.get().getResourceSettings().getLocalizer();
				if (game.getId() == null) {
					return localizer.getString("newGame", EditGamePage.this);
				} else {
					return localizer.getString("editGame", EditGamePage.this);
				}
			}
		}));

		Form<Void> form = new Form<Void>("form") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				scoreBoardService.saveGame(game);
				
				PageParameters pp = new PageParameters();
				pp.add("league", game.getLeague().getId());
				getRequestCycle().setResponsePage(DailyGamePage.class, pp);
			}
		};
    	add(form);
		
		form.add(new FeedbackPanel("feedback"));
    	
    	form.add(new DateField("gameDate", new PropertyModel<Date>(this, "game.date")));

    	form.add(new GameTeamPanel("team1", new PropertyModel<GameTeam>(this, "game.team1")));
    	form.add(new GameTeamPanel("team2", new PropertyModel<GameTeam>(this, "game.team2")));

    	IModel<League> defaultLeagueModel = new PropertyModel<League>(this, "game.league");
        form.add(new LeagueSelector("leagueField", defaultLeagueModel));
//		IModel<List<League>> possibleLeaguesModel = new LoadableDetachableModel<List<League>>() {
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			protected List<League> load() {
//				return scoreBoardService.getAllLeagues();
//			}
//		};
//		ChoiceRenderer<League> renderer = new ChoiceRenderer<League>("name", "id");
//
//		DropDownChoice<League> leagueField = new DropDownChoice<League>("leagueField", defaultLeagueModel, possibleLeaguesModel, renderer);
//		leagueField.add(new Select2Enabler());
//		form.add(leagueField);
	}

    public Game getGame() {
		return game;
	}

	private Game createNewGame() {
		long leagueId = 1;
		if (ScoreBoardSession.get().isAuthenticated()) {
			User user = ScoreBoardSession.get().getUser();
			League defaultLeague = user.getDefaultLeague();
			if (defaultLeague != null) {
				leagueId = defaultLeague.getId();
			}
		}
		League league = scoreBoardService.getLeague(leagueId);
		
		final Game game = new Game();
    	game.setDate(new Date());
    	GameTeam gameTeam1 = new GameTeam();
    	gameTeam1.setGame(game);
    	Team team1 = new Team();
		gameTeam1.setTeam(team1);
		game.setTeam1(gameTeam1);
    	GameTeam gameTeam2 = new GameTeam();
    	gameTeam2.setGame(game);
    	Team team2 = new Team();
		gameTeam2.setTeam(team2);
		game.setTeam2(gameTeam2);
		game.setLeague(league);
		return game;
	}

	@Override
	public MenuItemType getMenuItemType() {
		return MenuItemType.GAME;
	}

}
