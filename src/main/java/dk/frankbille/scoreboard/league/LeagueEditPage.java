package dk.frankbille.scoreboard.league;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import dk.frankbille.scoreboard.components.menu.MenuItemType;
import dk.frankbille.scoreboard.domain.League;
import dk.frankbille.scoreboard.security.SecureBasePage;
import dk.frankbille.scoreboard.service.ScoreBoardService;

public class LeagueEditPage extends SecureBasePage {
	private static final long serialVersionUID = 1L;

	@SpringBean
	private ScoreBoardService scoreBoardService;
	
	public LeagueEditPage(PageParameters pageParameters) {
		final long leagueId = pageParameters.get(0).toLong(-1);
		initialize(new LoadableDetachableModel<League>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected League load() {
				if (leagueId < 0) {
					return new League();
				} else {
					return scoreBoardService.getLeague(leagueId);
				}
			}
		});
	}
	
	private void initialize(IModel<League> leagueModel) {
		Form<League> playerForm = new Form<League>("leagueForm", leagueModel) {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onSubmit() {
				scoreBoardService.saveLeague(getModelObject());
				getRequestCycle().setResponsePage(LeagueListPage.class);
			}
		};
		add(playerForm);

		playerForm.add(new Label("name", new PropertyModel<String>(leagueModel, "name")));

		{
			TextField<String> nameField = new TextField<String>("nameField", new PropertyModel<String>(leagueModel, "name"));
			playerForm.add(nameField);
		}
	}

	@Override
	public MenuItemType getMenuItemType() {
		return MenuItemType.LEAGUES;
	}

}
