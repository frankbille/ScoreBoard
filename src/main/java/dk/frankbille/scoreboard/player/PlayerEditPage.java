package dk.frankbille.scoreboard.player;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponentLabel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import dk.frankbille.scoreboard.BasePage;
import dk.frankbille.scoreboard.components.menu.MenuPanel.MenuItemType;
import dk.frankbille.scoreboard.domain.Player;
import dk.frankbille.scoreboard.service.ScoreBoardService;

public class PlayerEditPage extends BasePage {
	private static final long serialVersionUID = 1L;

	@SpringBean
	private ScoreBoardService scoreBoardService;

	public PlayerEditPage(PageParameters parameters) {
		Long playerId = parameters.get(0).toLongObject();
		IModel<Player> playerModel = new PlayerModel(playerId);

		add(new Label("name", new PropertyModel<String>(playerModel, "name")));

		Form<Player> playerForm = new Form<Player>("playerForm", playerModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				scoreBoardService.savePlayer(getModelObject());
				getRequestCycle().setResponsePage(PlayerListPage.class);
			}
		};
		add(playerForm);

		{
			TextField<String> nameField = new TextField<String>("nameField", new PropertyModel<String>(playerModel, "name"));
			playerForm.add(nameField);
			FormComponentLabel nameLabel = new FormComponentLabel("nameLabel", nameField);
			playerForm.add(nameLabel);
		}

		{
			TextField<String> fullNameField = new TextField<String>("fullNameField", new PropertyModel<String>(playerModel, "fullName"));
			playerForm.add(fullNameField);
			FormComponentLabel fullNameLabel = new FormComponentLabel("fullNameLabel", fullNameField);
			playerForm.add(fullNameLabel);
		}

		{
			TextField<String> groupField = new TextField<String>("groupField", new PropertyModel<String>(playerModel, "groupName"));
			playerForm.add(groupField);
			FormComponentLabel groupLabel = new FormComponentLabel("groupLabel", groupField);
			playerForm.add(groupLabel);
		}
	}

	@Override
	public MenuItemType getMenuItemType() {
		return MenuItemType.PLAYERS;
	}

}
