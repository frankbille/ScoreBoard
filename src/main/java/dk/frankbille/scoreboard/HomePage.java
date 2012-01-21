package dk.frankbille.scoreboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DateField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import dk.frankbille.scoreboard.domain.Game;
import dk.frankbille.scoreboard.domain.GameTeam;
import dk.frankbille.scoreboard.domain.Player;
import dk.frankbille.scoreboard.domain.PlayerResult;
import dk.frankbille.scoreboard.domain.Team;
import dk.frankbille.scoreboard.service.ScoreBoardService;

public class HomePage extends WebPage {
	private static final long serialVersionUID = 1L;

	@SpringBean
	private ScoreBoardService scoreBoardService;

	private WebMarkupContainer gamesContainer;

	private Game game;

	private WebMarkupContainer newGameContainer;

	private WebMarkupContainer playersContainer;

    public HomePage(final PageParameters parameters) {
    	addNewGame();

    	addGameResults();

		playersContainer = new WebMarkupContainer("playersContainer");
		playersContainer.setOutputMarkupId(true);
		add(playersContainer);

		IModel<List<PlayerResult>> playerResultsModel = new LoadableDetachableModel<List<PlayerResult>>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<PlayerResult> load() {
				List<PlayerResult> playerResults = scoreBoardService.getPlayerResults();

				Collections.sort(playerResults, new Comparator<PlayerResult>() {
					@Override
					public int compare(PlayerResult o1, PlayerResult o2) {
						int compare = 0;

						compare = new Double(o2.getGamesWonRatio()).compareTo(o1.getGamesWonRatio());

						if (compare == 0) {
							compare = new Integer(o2.getGamesWon()).compareTo(o1.getGamesWon());
						}

						if (compare == 0) {
							compare = new Integer(o1.getGamesLost()).compareTo(o2.getGamesLost());
						}

						if (compare == 0) {
							compare = o1.getPlayer().getName().compareTo(o2.getPlayer().getName());
						}

						if (compare == 0) {
							compare = o1.getPlayer().getId().compareTo(o2.getPlayer().getId());
						}

						return compare;
					}
				});

				return playerResults;
			}
		};

		playersContainer.add(new ListView<PlayerResult>("players", playerResultsModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<PlayerResult> item) {
				item.add(new Label("name", new PropertyModel<Integer>(item.getModel(), "player.name")));
				item.add(new Label("gamesWon", new PropertyModel<Integer>(item.getModel(), "gamesWon")));
				item.add(new Label("gamesLost", new PropertyModel<Integer>(item.getModel(), "gamesLost")));
				item.add(new Label("winRatio", new PropertyModel<Double>(item.getModel(), "gamesWonRatio")));
			}
		});
    }

	private void addNewGame() {
		game = createNewGame();

    	newGameContainer = new WebMarkupContainer("newGameContainer");
    	newGameContainer.setOutputMarkupId(true);
    	add(newGameContainer);

    	Form<Void> dateForm = new Form<Void>("dateForm");
		dateForm.add(new DateField("gameDate", new PropertyModel<Date>(this, "game.date")) {
			private static final long serialVersionUID = 1L;

			@Override
			protected DateTextField newDateTextField(String id, PropertyModel<Date> dateFieldModel) {
				DateTextField dateTextField = super.newDateTextField(id, dateFieldModel);
				dateTextField.add(new AjaxFormSubmitBehavior("onchange") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onSubmit(AjaxRequestTarget target) {
					}

					@Override
					protected void onError(AjaxRequestTarget target) {
					}
				});
				return dateTextField;
			}
		});
    	newGameContainer.add(dateForm);

    	newGameContainer.add(new ListView<GameTeam>("teams", new PropertyModel<List<GameTeam>>(this, "game.teams")) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<GameTeam> item) {
				item.add(new GameTeamPanel("team", item.getModel()));
			}
		});

    	newGameContainer.add(new AjaxLink<Void>("save") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				scoreBoardService.saveGame(game);
				game = createNewGame();
				target.add(newGameContainer);
				target.add(gamesContainer);
				target.add(playersContainer);
			}
		});
	}

	private void addGameResults() {
		IModel<List<Game>> gamesModel = new LoadableDetachableModel<List<Game>>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<Game> load() {
				return scoreBoardService.getAllGames();
			}
		};

		gamesContainer = new WebMarkupContainer("gamesContainer");
		gamesContainer.setOutputMarkupId(true);
		add(gamesContainer);

		gamesContainer.add(new ListView<Game>("games", gamesModel) {
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

    public Game getGame() {
		return game;
	}

	private Game createNewGame() {
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
		return game;
	}
}
