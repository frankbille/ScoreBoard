package dk.frankbille.scoreboard.daily;

import java.util.Collection;

import org.apache.wicket.Application;
import org.apache.wicket.Localizer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.vaynberg.wicket.select2.Select2MultiChoice;

import dk.frankbille.scoreboard.domain.GameTeam;
import dk.frankbille.scoreboard.domain.Player;
import dk.frankbille.scoreboard.service.ScoreBoardService;

public class GameTeamPanel extends GenericPanel<GameTeam> {
	private static final long serialVersionUID = 1L;

	private static class PlayersModel extends AbstractReadOnlyModel<Collection<Player>> {
		private static final long serialVersionUID = 1L;

		private IModel<GameTeam> gameTeamModel;
		
		public PlayersModel(IModel<GameTeam> gameTeamModel) {
			this.gameTeamModel = gameTeamModel;
		}

		@Override
		public void detach() {
			gameTeamModel.detach();
		}

		@Override
		public Collection<Player> getObject() {
			GameTeam gameTeam = gameTeamModel.getObject();
			return gameTeam.getTeam().getPlayers();
		}
	};

	@SpringBean
	private ScoreBoardService scoreBoardService;

	public GameTeamPanel(String id, IModel<GameTeam> model) {
		super(id, model);

		add(new Label("teamName", new PropertyModel<String>(model, "team.name")));

		TextField<Integer> scoreField = new TextField<Integer>("score", new PropertyModel<Integer>(model, "score"));
		scoreField.add(new AjaxFormSubmitBehavior("onchange") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
			}

			@Override
			protected void onError(AjaxRequestTarget target) {
			}
		});
		add(scoreField);
		
		final Select2MultiChoice<Player> players = new Select2MultiChoice<Player>("players", new PlayersModel(model), new PlayersProvider());
		players.getSettings().setMinimumInputLength(2);
		Localizer localizer = Application.get().getResourceSettings().getLocalizer();
		String locString = localizer.getString("playerSearchTermTooShort", null);
		locString = "'"+locString+"'";
		locString = locString.replace("{term}", "'+term+'");
		locString = locString.replace("{minLength}", "'+(minLength-term.length)+'");
		players.getSettings().setFormatInputTooShort("function(term, minLength){return "+locString+"}");
		add(players);
	}

}
