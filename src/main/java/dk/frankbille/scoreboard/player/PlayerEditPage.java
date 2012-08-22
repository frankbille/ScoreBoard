package dk.frankbille.scoreboard.player;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import dk.frankbille.scoreboard.components.menu.MenuPanel.MenuItemType;
import dk.frankbille.scoreboard.domain.Player;
import dk.frankbille.scoreboard.security.SecureBasePage;
import dk.frankbille.scoreboard.service.ScoreBoardService;

public class PlayerEditPage extends SecureBasePage {
	private static final long serialVersionUID = 1L;

	@SpringBean
	private ScoreBoardService scoreBoardService;
	
	public PlayerEditPage(PageParameters parameters) {
		Long playerId = parameters.get(0).toLongObject();
		IModel<Player> playerModel = new PlayerModel(playerId);
		initialize(playerModel);
	}

	public PlayerEditPage(IModel<Player> playerModel) {
		initialize(playerModel);
	}
	
	private void initialize(IModel<Player> playerModel) {
		Form<Player> playerForm = new Form<Player>("playerForm", playerModel) {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onSubmit() {
				scoreBoardService.savePlayer(getModelObject());
				getRequestCycle().setResponsePage(PlayerListPage.class);
			}
		};
		add(playerForm);

		playerForm.add(new Label("name", new PropertyModel<String>(playerModel, "name")));

		{
			TextField<String> nameField = new TextField<String>("nameField", new PropertyModel<String>(playerModel, "name"));
			playerForm.add(nameField);
		}

		{
			TextField<String> fullNameField = new TextField<String>("fullNameField", new PropertyModel<String>(playerModel, "fullName"));
			playerForm.add(fullNameField);
		}

		{
			TextField<String> groupField = new TextField<String>("groupField", new PropertyModel<String>(playerModel, "groupName"));
			playerForm.add(groupField);
		}
	}

	@Override
	public MenuItemType getMenuItemType() {
		return MenuItemType.PLAYERS;
	}

}
