package dk.frankbille.scoreboard.components;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import dk.frankbille.scoreboard.comparators.PlayerComparator;
import dk.frankbille.scoreboard.domain.GameTeam;
import dk.frankbille.scoreboard.domain.Player;
import dk.frankbille.scoreboard.domain.Team;
import dk.frankbille.scoreboard.player.PlayerPage;
import dk.frankbille.scoreboard.ratings.GamePlayerRating;
import dk.frankbille.scoreboard.ratings.GameRating;
import dk.frankbille.scoreboard.ratings.RatingCalculator;
import dk.frankbille.scoreboard.ratings.RatingProvider;

public class GameTeamPanel extends Panel {
	private static final long serialVersionUID = 1L;

	private static final DecimalFormat RATING_VALUE = new DecimalFormat("#,##0");
	private static final DecimalFormat RATING_CHANGE = new DecimalFormat("+0.0;-0.0");

	public GameTeamPanel(String id, final IModel<GameTeam> model, final IModel<Player> selectedPlayerModel) {
		super(id, model);

		IModel<List<Player>> playersModel = new LoadableDetachableModel<List<Player>>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<Player> load() {
				GameTeam gameTeam = model.getObject();
				Team team = gameTeam.getTeam();
				List<Player> players = new ArrayList<Player>(team.getPlayers());
				Collections.sort(players, new PlayerComparator());
				return players;
			}
		};

		add(new ListView<Player>("players", playersModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Player> item) {
				final Player player = item.getModelObject();

				RatingCalculator rating = RatingProvider.getRatings();
				GamePlayerRating playerRating = rating.getGamePlayerRating(model.getObject().getGame().getId(), player.getId());

				PageParameters pp = new PageParameters();
				pp.set(0, player.getId());
				BookmarkablePageLink<Void> playerLink = new BookmarkablePageLink<Void>("playerLink", PlayerPage.class, pp);
				AttributeAppender highlightModifier = new AttributeAppender("class", "highlighted") {
					private static final long serialVersionUID = 1L;

					@Override
					public boolean isEnabled(Component component) {
						return selectedPlayerModel.getObject().equals(player);
					}
				};
				highlightModifier.setSeparator(" ");
				playerLink.add(highlightModifier);
				item.add(playerLink);
				playerLink.add(new Label("name", new PropertyModel<String>(item.getModel(), "name")));
				item.add(new Label("rating", new FormatModel(RATING_VALUE, playerRating.getRating())));
			}
		});

		final IModel<GameRating> gameRatingModel = new LoadableDetachableModel<GameRating>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected GameRating load() {
				GameTeam gameTeam = model.getObject();
				RatingCalculator rating = RatingProvider.getRatings();
				return rating.getGameRatingChange(gameTeam.getGame().getId());
			}
		};

		IModel<Double> teamRatingModel = new AbstractReadOnlyModel<Double>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Double getObject() {
				GameTeam gameTeam = model.getObject();

				GameRating gameRating = gameRatingModel.getObject();

				if (gameTeam.isWinner()) {
					return gameRating.getWinnerRating();
				} else {
					return gameRating.getLoserRating();
				}
			}
		};

		IModel<Double> teamRatingChangeModel = new AbstractReadOnlyModel<Double>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Double getObject() {
				GameTeam gameTeam = model.getObject();

				GameRating gameRating = gameRatingModel.getObject();

				double change = gameRating.getChange();
				if (false == gameTeam.isWinner()) {
					change = 0 - change;
				}

				int playerCount = gameTeam.getTeam().getPlayers().size();
				if (playerCount > 0) {
					change /= playerCount;
				}

				return change;
			}
		};

		add(new Label("teamRating", new FormatModel(RATING_VALUE, teamRatingModel)));
		add(new Label("teamRatingChange", new FormatModel(RATING_CHANGE, teamRatingChangeModel)));
	}

}
