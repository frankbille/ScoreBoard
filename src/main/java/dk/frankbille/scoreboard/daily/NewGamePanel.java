package dk.frankbille.scoreboard.daily;

import java.util.Date;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DateField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import dk.frankbille.scoreboard.domain.Game;
import dk.frankbille.scoreboard.domain.GameTeam;
import dk.frankbille.scoreboard.domain.Team;
import dk.frankbille.scoreboard.security.RequiresLoginToRender;
import dk.frankbille.scoreboard.service.ScoreBoardService;

public abstract class NewGamePanel extends Panel implements RequiresLoginToRender {
	private static final long serialVersionUID = 1L;

	@SpringBean
	private ScoreBoardService scoreBoardService;

	private Game game;

	public NewGamePanel(String id) {
		super(id);
		setOutputMarkupId(true);

		game = createNewGame();

    	Form<Void> form = new Form<Void>("form");
		form.add(new DateField("gameDate", new PropertyModel<Date>(this, "game.date")) {
			private static final long serialVersionUID = 1L;

			@Override
			protected DateTextField newDateTextField(String id, PropertyModel<Date> dateFieldModel) {
				DateTextField dateTextField = DateTextField.forDatePattern(id, dateFieldModel, "yyyy-MM-dd");
				return dateTextField;
			}
		});
    	add(form);

    	form.add(new ListView<GameTeam>("teams", new PropertyModel<List<GameTeam>>(this, "game.teams")) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<GameTeam> item) {
				item.add(new GameTeamPanel("team", item.getModel()));
			}
		});

    	form.add(new AjaxSubmitLink("save") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				scoreBoardService.saveGame(game);
				Game addedGame = game;
				game = createNewGame();
				target.add(NewGamePanel.this);
				newGameAdded(addedGame, target);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				// TODO Auto-generated method stub
				
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

	protected abstract void newGameAdded(Game game, AjaxRequestTarget target);

}
