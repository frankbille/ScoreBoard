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

package dk.frankbille.scoreboard.user;

import java.util.List;

import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import dk.frankbille.scoreboard.components.Select2Enabler;
import dk.frankbille.scoreboard.domain.League;
import dk.frankbille.scoreboard.domain.User;
import dk.frankbille.scoreboard.service.ScoreBoardService;

public class UserPanel extends Panel {
	private static final long serialVersionUID = 1L;
	
	@SpringBean
	private ScoreBoardService scoreBoardService;

	public UserPanel(String id, IModel<User> model) {
		super(id, model);
		
		TextField<String> usernameField = new TextField<String>("usernameField", new PropertyModel<String>(model, "username"));
		usernameField.add(new UsernameValidator(model));
		usernameField.setRequired(true);
		add(usernameField);

		IModel<League> defaultLeagueModel = new PropertyModel<League>(model, "defaultLeague");
		IModel<List<League>> possibleLeaguesModel = new LoadableDetachableModel<List<League>>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<League> load() {
				return scoreBoardService.getAllLeagues();
			}
		};
		ChoiceRenderer<League> renderer = new ChoiceRenderer<League>("name", "id");
		DropDownChoice<League> defaultLeagueField = new DropDownChoice<League>("defaultLeagueField", defaultLeagueModel, possibleLeaguesModel, renderer);
		defaultLeagueField.add(new Select2Enabler());
		add(defaultLeagueField);
	}

}
