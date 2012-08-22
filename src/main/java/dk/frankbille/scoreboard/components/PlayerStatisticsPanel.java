package dk.frankbille.scoreboard.components;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.wicket.Application;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Localizer;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import dk.frankbille.scoreboard.domain.Player;
import dk.frankbille.scoreboard.domain.PlayerResult;
import dk.frankbille.scoreboard.ratings.RatingCalculator;
import dk.frankbille.scoreboard.ratings.RatingProvider;
import dk.frankbille.scoreboard.service.ScoreBoardService;

public class PlayerStatisticsPanel extends Panel {
	private static final long serialVersionUID = 1L;

	@SpringBean
	private ScoreBoardService scoreBoardService;

	public PlayerStatisticsPanel(String id) {
		this(id, PlayedGameListPanel.createNoSelectedPlayerModel());
	}

	public PlayerStatisticsPanel(String id, final IModel<Player> selectedPlayerModel) {
		super(id);

		IModel<List<PlayerResult>> playerResultsModel = new LoadableDetachableModel<List<PlayerResult>>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<PlayerResult> load() {
				List<PlayerResult> playerResults = scoreBoardService.getPlayerResults();
				final RatingCalculator rating = RatingProvider.getRatings();

				Collections.sort(playerResults, new Comparator<PlayerResult>() {
					@Override
					public int compare(PlayerResult o1, PlayerResult o2) {
						int compare = 0;

						double rating1 = rating.getPlayerRating(o1.getPlayer().getId()).getRating();
						Double rating2 = rating.getPlayerRating(o2.getPlayer().getId()).getRating();
						compare = rating2.compareTo(rating1);

						if (compare == 0) {
							new Double(o2.getGamesWonRatio()).compareTo(o1.getGamesWonRatio());
						}

						if (compare == 0) {
							compare = new Integer(o2.getGamesWon()).compareTo(o1.getGamesWon());
						}

						if (compare == 0) {
							compare = new Integer(o1.getGamesLost()).compareTo(o2.getGamesLost());
						}

						if (compare == 0) {
							compare = new Integer(o1.getGamesCount()).compareTo(o2.getGamesCount());
						}

						if (compare == 0) {
							compare = o1.getPlayer().getName().compareTo(o2.getPlayer().getName());
						}

						if (compare == 0) {
							compare = o1.getPlayer().getId().compareTo(o2.getPlayer().getId());
						}

						return compare;
					}
				});

				return playerResults;
			}
		};

		add(new ListView<PlayerResult>("players", playerResultsModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<PlayerResult> item) {
				final PlayerResult playerResult = item.getModelObject();
				Player player = playerResult.getPlayer();
				Player selectedPlayer = selectedPlayerModel.getObject();

				item.add(AttributeModifier.replace("title", new AbstractReadOnlyModel<CharSequence>() {
					private static final long serialVersionUID = 1L;

					@Override
					public CharSequence getObject() {
						StringBuilder b = new StringBuilder();
						Localizer localizer = Application.get().getResourceSettings().getLocalizer();
						b.append(localizer.getString("won", item)).append(": ").append(playerResult.getGamesWon());
						b.append(", ");
						b.append(localizer.getString("lost", item)).append(": ").append(playerResult.getGamesLost());
						return b;
					}
				}));

				item.add(RowColorModifier.create(item));
				if (selectedPlayer != null && player.getId().equals(selectedPlayer.getId())) {
					item.add(new AttributeAppender("class", new Model<String>("highlighted"), " "));
				}
				item.add(new Label("number", ""+(item.getIndex()+1)));
				item.add(new Label("name", new PropertyModel<Integer>(item.getModel(), "player.name")));
				item.add(new Label("gamesCount", new PropertyModel<Integer>(item.getModel(), "gamesCount")));
				item.add(new Label("winRatio", new FormatModel(new DecimalFormat("0.00"), new PropertyModel<Double>(item.getModel(), "gamesWonRatio"))));
				item.add(new Label("rating", new FormatModel(new DecimalFormat("#"), new PropertyModel<Double>(item.getModel(), "rating"))));
				item.add(new Label("trend", new StringResourceModel(item.getModelObject().getTrend().name().toLowerCase(), null)));
			}
		});
	}

}
