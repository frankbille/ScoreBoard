package dk.frankbille.scoreboard.daily;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DateField;
import org.apache.wicket.markup.html.WebMarkupContainer;
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

import dk.frankbille.scoreboard.BasePage;
import dk.frankbille.scoreboard.comparators.GameComparator;
import dk.frankbille.scoreboard.comparators.GameTeamComparator;
import dk.frankbille.scoreboard.comparators.PlayerComparator;
import dk.frankbille.scoreboard.components.PlayerStatisticsPanel;
import dk.frankbille.scoreboard.components.RowColorModifier;
import dk.frankbille.scoreboard.components.menu.MenuPanel.MenuItemType;
import dk.frankbille.scoreboard.domain.Game;
import dk.frankbille.scoreboard.domain.GameTeam;
import dk.frankbille.scoreboard.domain.Player;
import dk.frankbille.scoreboard.domain.Team;
import dk.frankbille.scoreboard.service.ScoreBoardService;

public class DailyGamePage extends BasePage {
	private static final long serialVersionUID = 1L;

	@SpringBean
	private ScoreBoardService scoreBoardService;

	private WebMarkupContainer gamesContainer;

	private Game game;

	private WebMarkupContainer newGameContainer;

	private PlayerStatisticsPanel playersContainer;

    public DailyGamePage(final PageParameters parameters) {
    	addNewGame();

    	addGameResults();

		addPlayerStatistics();
    }

    @Override
    public MenuItemType getMenuItemType() {
    	return MenuItemType.DAILY;
    }

	private void addPlayerStatistics() {
		playersContainer = new PlayerStatisticsPanel("playersContainer");
		playersContainer.setOutputMarkupId(true);
		add(playersContainer);
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
				DateTextField dateTextField = DateTextField.forDatePattern(id, dateFieldModel, "yyyy-MM-dd");
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
				List<Game> allGames = scoreBoardService.getAllGames();
				Collections.sort(allGames, new GameComparator());
				return allGames;
			}
		};

		gamesContainer = new WebMarkupContainer("gamesContainer");
		gamesContainer.setOutputMarkupId(true);
		add(gamesContainer);

		gamesContainer.add(new ListView<Game>("games", gamesModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<Game> item) {
				item.add(RowColorModifier.create(item));
				item.add(new Label("date", ""+item.getModelObject().getDate()));
				item.add(new Label("teams", new AbstractReadOnlyModel<String>() {
					private static final long serialVersionUID = 1L;

					@Override
					public String getObject() {
						DecimalFormat df = new DecimalFormat("#.#");
						
						StringBuilder b = new StringBuilder();
						Game game = item.getModelObject();
						List<GameTeam> teams = game.getTeams();
						Collections.sort(teams, new GameTeamComparator());
						for (GameTeam gameTeam : teams) {
							if (b.length() > 0) {
								b.append(" vs. ");
							}

							StringBuilder t = new StringBuilder();
							List<Player> players = new ArrayList<Player>(gameTeam.getTeam().getPlayers());
							Collections.sort(players, new PlayerComparator());
							for (Player player : players) {
								if (t.length() > 0) {
									t.append("/");
								}
								t.append(player.getName());
							}
							t.append(" (");
							t.append(df.format(gameTeam.getRating()));
							t.append(")");
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
						double ratingChange = 0;
						Collections.sort(teams, new GameTeamComparator());
						for (GameTeam gameTeam : teams) {
							if (b.length() > 0) {
								b.append(" : ");
							}
							else {
								ratingChange = gameTeam.getRatingChange();
							}

							b.append(gameTeam.getScore());
						}
						b.append(" (+");
						b.append(new DecimalFormat("#.#").format(ratingChange));
						b.append(")");
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
