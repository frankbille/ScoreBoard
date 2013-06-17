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

import java.util.Collection;

import org.apache.wicket.Application;
import org.apache.wicket.Localizer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

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

		setRenderBodyOnly(true);

		TextField<Integer> scoreField = new TextField<Integer>("score", new PropertyModel<Integer>(model, "score"));
		scoreField.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget arg0) {
			}
		});
		add(scoreField);

		Localizer localizer = Application.get().getResourceSettings().getLocalizer();

		final Select2MultiChoice<Player> players = new Select2MultiChoice<Player>("players", new PlayersModel(model), new PlayersProvider());
		players.getSettings().setMinimumInputLength(2);
		players.getSettings().setContainerCssClass("span3");
		players.getSettings().setPlaceholder(localizer.getString("players", null));
		String locString = localizer.getString("playerSearchTermTooShort", null);
		locString = "'"+locString+"'";
		locString = locString.replace("{term}", "'+term+'");
		locString = locString.replace("{minLength}", "'+(minLength-term.length)+'");
		players.getSettings().setFormatInputTooShort("function(term, minLength){return "+locString+"}");
		players.add(new IValidator<Collection<Player>>() {
			private static final long serialVersionUID = 1L;

			@Override
			public void validate(IValidatable<Collection<Player>> validatable) {
				Collection<Player> value = validatable.getValue();
				if (value.isEmpty()) {
					validatable.error(new ValidationError().addKey("teamMustHaveAtLeastOnePlayer"));
				}
			}
		});
		add(players);
	}

}
