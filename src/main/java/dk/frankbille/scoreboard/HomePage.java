package dk.frankbille.scoreboard;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import dk.frankbille.scoreboard.domain.Game;
import dk.frankbille.scoreboard.domain.GameTeam;
import dk.frankbille.scoreboard.domain.Player;
import dk.frankbille.scoreboard.domain.Team;
import dk.frankbille.scoreboard.service.ScoreBoardService;

public class HomePage extends WebPage {
	private static final long serialVersionUID = 1L;

	@SpringBean
	private ScoreBoardService scoreBoardService;

    public HomePage(final PageParameters parameters) {
    	final Game game = new Game();
    	game.setDate(new Date());
    	GameTeam gameTeam1 = new GameTeam();
    	gameTeam1.setGame(game);
    	Team team1 = new Team();
		gameTeam1.setTeam(team1);
		game.addTeam(gameTeam1);
    	GameTeam gameTeam2 = new GameTeam();
    	gameTeam2.setGame(game);
    	Team team2 = new Team();
		gameTeam2.setTeam(team2);
		game.addTeam(gameTeam2);

		add(new ListView<GameTeam>("teams", game.getTeams()) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<GameTeam> item) {
				item.add(new GameTeamPanel("team", item.getModel()));
			}
		});

    	add(new AjaxLink<Void>("save") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				scoreBoardService.saveGame(game);
			}
		});

    	/*
    	 * Games
    	 */
    	IModel<List<Game>> gamesModel = new LoadableDetachableModel<List<Game>>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<Game> load() {
				return scoreBoardService.getAllGames();
			}
		};

		add(new ListView<Game>("games", gamesModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<Game> item) {
				item.add(new Label("date", ""+item.getModelObject().getDate()));
				item.add(new Label("teams", new AbstractReadOnlyModel<String>() {
					private static final long serialVersionUID = 1L;

					@Override
					public String getObject() {
						StringBuilder b = new StringBuilder();
						Game game = item.getModelObject();
						List<GameTeam> teams = game.getTeams();
						for (GameTeam gameTeam : teams) {
							if (b.length() > 0) {
								b.append(" vs. ");
							}

							StringBuilder t = new StringBuilder();
							List<Player> players = new ArrayList<Player>(gameTeam.getTeam().getPlayers());
							for (Player player : players) {
								if (t.length() > 0) {
									t.append("/");
								}
								t.append(player.getName());
							}
							b.append(t);
						}
						return b.toString();
					}
				}));
				item.add(new Label("score", new AbstractReadOnlyModel<String>() {
					private static final long serialVersionUID = 1L;

					@Override
					public String getObject() {
						StringBuilder b = new StringBuilder();
						Game game = item.getModelObject();
						List<GameTeam> teams = game.getTeams();
						for (GameTeam gameTeam : teams) {
							if (b.length() > 0) {
								b.append(" : ");
							}

							b.append(gameTeam.getScore());
						}
						return b.toString();
					}
				}));
			}
		});
    }
}
