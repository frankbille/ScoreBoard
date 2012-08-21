package dk.frankbille.scoreboard.components;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import dk.frankbille.scoreboard.comparators.GameTeamComparator;
import dk.frankbille.scoreboard.domain.Game;
import dk.frankbille.scoreboard.domain.GameTeam;
import dk.frankbille.scoreboard.domain.Player;
import dk.frankbille.scoreboard.security.SecureExecutionAjaxLink;

public class PlayedGameListPanel extends Panel {
	private static final long serialVersionUID = 1L;
	
	public static interface GameSelectedCallback extends Serializable {
		void onSelection(AjaxRequestTarget target, Game game);
	}

	public static IModel<Player> createNoSelectedPlayerModel() {
		return new LoadableDetachableModel<Player>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected Player load() {
				Player player = new Player();
				player.setId(Long.MIN_VALUE);
				return player;
			}
		};
	}

	public PlayedGameListPanel(String id, IModel<List<Game>> gamesModel) {
		this(id, gamesModel, createNoSelectedPlayerModel());
	}

	public PlayedGameListPanel(String id, IModel<List<Game>> gamesModel, final IModel<Player> selectedPlayerModel) {
		this(id, gamesModel, selectedPlayerModel, null);
	}
	
	public PlayedGameListPanel(String id, IModel<List<Game>> gamesModel, final IModel<Player> selectedPlayerModel, final GameSelectedCallback gameSelectedCallback) {
		super(id);

		add(new ListView<Game>("games", gamesModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<Game> item) {
				item.add(RowColorModifier.create(item));
				WebMarkupContainer link = null;
				if (gameSelectedCallback != null) {
					link = new SecureExecutionAjaxLink<Game>("gameLink", item.getModel()) {
						private static final long serialVersionUID = 1L;

						@Override
						public void onClick(AjaxRequestTarget target) {
							gameSelectedCallback.onSelection(target, getModelObject());
						}
					};
				} else {
					link = new WebMarkupContainer("gameLink");
				}
				item.add(link);
				
				link.add(new DateLabel("date", new PropertyModel<Date>(item.getModel(), "date"), new PatternDateConverter("yyyy-MM-dd", false)));

				//Add the winning and losing team
				Game game = item.getModelObject();
				List<GameTeam> teams = game.getTeams();
				Collections.sort(teams, new GameTeamComparator());
				for (GameTeam gameTeam : teams) {
					if (gameTeam.isWinner()) {
						item.add(new GameTeamPanel("winner", new Model<GameTeam>(gameTeam), selectedPlayerModel));
					}
					else {
						item.add(new GameTeamPanel("loser", new Model<GameTeam>(gameTeam), selectedPlayerModel));
					}
				}

				//Add the game score
				item.add(new Label("score", new AbstractReadOnlyModel<String>() {
					private static final long serialVersionUID = 1L;

					@Override
					public String getObject() {
						StringBuilder b = new StringBuilder();
						Game game = item.getModelObject();
						List<GameTeam> teams = game.getTeams();
						Collections.sort(teams, new GameTeamComparator());
						for (GameTeam gameTeam : teams) {
							if (b.length() > 0) {
								b.append(" : ");
							}

							b.append(gameTeam.getScore());
						}
						return b.toString();
					}
				}));
			}
		});
	}

}
