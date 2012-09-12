package dk.frankbille.scoreboard.daily;

import java.util.Date;

import org.apache.wicket.Application;
import org.apache.wicket.Localizer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import dk.frankbille.scoreboard.domain.Game;
import dk.frankbille.scoreboard.domain.GameTeam;
import dk.frankbille.scoreboard.domain.Team;
import dk.frankbille.scoreboard.security.RequiresLoginToRender;
import dk.frankbille.scoreboard.service.ScoreBoardService;

public abstract class EditGamePanel extends Panel implements RequiresLoginToRender {
	private static final long serialVersionUID = 1L;

	@SpringBean
	private ScoreBoardService scoreBoardService;

	private Game game;

	public EditGamePanel(String id) {
		this(id, null);
	}
	
	public EditGamePanel(String id, Long gameId) {
		super(id);
		setOutputMarkupId(true);

		if (gameId != null) {
			game = scoreBoardService.getGame(gameId);
		} else {
			game = createNewGame();
		}

    	Form<Void> form = new Form<Void>("form");
    	add(form);
		
		form.add(new Label("editGameTitle", new AbstractReadOnlyModel<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getObject() {
				Localizer localizer = Application.get().getResourceSettings().getLocalizer();
				if (game.getId() == null) {
					return localizer.getString("newGame", EditGamePanel.this);
				} else {
					return localizer.getString("editGame", EditGamePanel.this);
				}
			}
		}));
		
		form.add(new FeedbackPanel("feedback"));
    	
    	form.add(DateTextField.forDatePattern("gameDate", new PropertyModel<Date>(this, "game.date"), "yyyy-MM-dd"));

    	form.add(new GameTeamPanel("team1", new PropertyModel<GameTeam>(this, "game.team1")));
    	form.add(new GameTeamPanel("team2", new PropertyModel<GameTeam>(this, "game.team2")));

    	form.add(new AjaxSubmitLink("save") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				scoreBoardService.saveGame(game);
				Game addedGame = game;
				game = createNewGame();
				target.add(EditGamePanel.this);
				newGameAdded(addedGame, target);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.add(EditGamePanel.this);
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
		game.setTeam1(gameTeam1);
    	GameTeam gameTeam2 = new GameTeam();
    	gameTeam2.setGame(game);
    	Team team2 = new Team();
		gameTeam2.setTeam(team2);
		game.setTeam2(gameTeam2);
		return game;
	}

	protected abstract void newGameAdded(Game game, AjaxRequestTarget target);

}
