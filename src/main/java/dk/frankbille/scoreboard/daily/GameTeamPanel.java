package dk.frankbille.scoreboard.daily;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.ajax.calldecorator.AjaxCallDecorator;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponentLabel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;

import dk.frankbille.scoreboard.domain.GameTeam;
import dk.frankbille.scoreboard.domain.Player;
import dk.frankbille.scoreboard.service.ScoreBoardService;

public class GameTeamPanel extends GenericPanel<GameTeam> {
	private static final long serialVersionUID = 1L;

	private static class PlayerSelectionModel extends Model<Boolean> {
		private static final long serialVersionUID = 1L;

		private Player player;
		private IModel<? extends Collection<Player>> selectedPlayersModel;

		public PlayerSelectionModel(Player player, IModel<? extends Collection<Player>> selectedPlayersModel) {
			this.player = player;
			this.selectedPlayersModel = selectedPlayersModel;
		}

		@Override
		public Boolean getObject() {
			Collection<Player> players = selectedPlayersModel.getObject();
			return players.contains(player);
		}

		@Override
		public void setObject(Boolean selected) {
			Collection<Player> players = selectedPlayersModel.getObject();
			players.remove(player);

			if (selected) {
				players.add(player);
			}
		}

	}

	@SpringBean
	private ScoreBoardService scoreBoardService;

	public GameTeamPanel(String id, IModel<GameTeam> model) {
		super(id, model);

		add(new Label("teamName", new PropertyModel<String>(model, "team.name")));

		Form<Void> scoreForm = new Form<Void>("scoreForm");
		add(scoreForm);

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
		scoreForm.add(scoreField);

		final IModel<Set<Player>> selectedPlayersModel = new PropertyModel<Set<Player>>(model, "team.players");

		final WebMarkupContainer players = new WebMarkupContainer("players");
		players.setOutputMarkupId(true);
		add(players);

		final IModel<List<Player>> playerListModel = new LoadableDetachableModel<List<Player>>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<Player> load() {
				return scoreBoardService.getAllPlayers();
			}
		};

		players.add(new ListView<Player>("possiblePlayers", playerListModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Player> item) {
				CheckBox check = new CheckBox("player", new PlayerSelectionModel(item.getModelObject(), selectedPlayersModel));
				check.add(new AjaxFormComponentUpdatingBehavior("onclick") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
					}
				});
				item.add(check);

				FormComponentLabel label = new FormComponentLabel("playerName", check);
				item.add(label);

				label.add(new Label("name", new PropertyModel<String>(item.getModel(), "name")));
			}
		});


		IModel<String> newPlayerModel = new Model<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			public void setObject(String name) {
				if (Strings.isEmpty(name) == false) {
					Player player = scoreBoardService.createNewPlayer(name);
					selectedPlayersModel.getObject().add(player);
				}
			}
		};
		final TextField<String> newPlayerNameField = new TextField<String>("newPlayerName", newPlayerModel);

		Form<Void> newPlayerForm = new Form<Void>("newPlayerForm");
		newPlayerForm.add(new AjaxFormSubmitBehavior("onsubmit") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				target.add(newPlayerNameField);
				target.add(players);
				target.focusComponent(newPlayerNameField);
			}

			@Override
			protected void onError(AjaxRequestTarget target) {
			}

			@Override
			protected IAjaxCallDecorator getAjaxCallDecorator() {
				return new AjaxCallDecorator() {
					private static final long serialVersionUID = 1L;

					@Override
					public CharSequence decorateScript(Component c, CharSequence script) {
						return script + "; return false;";
					}
				};
			}
		});
		players.add(newPlayerForm);
		newPlayerNameField.setOutputMarkupId(true);
		newPlayerForm.add(newPlayerNameField);
	}

}
