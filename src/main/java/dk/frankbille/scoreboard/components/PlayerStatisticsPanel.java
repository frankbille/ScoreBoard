package dk.frankbille.scoreboard.components;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import dk.frankbille.scoreboard.domain.PlayerResult;
import dk.frankbille.scoreboard.service.ScoreBoardService;

public class PlayerStatisticsPanel extends Panel {
	private static final long serialVersionUID = 1L;

	@SpringBean
	private ScoreBoardService scoreBoardService;

	public PlayerStatisticsPanel(String id) {
		super(id);

		IModel<List<PlayerResult>> playerResultsModel = new LoadableDetachableModel<List<PlayerResult>>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<PlayerResult> load() {
				List<PlayerResult> playerResults = scoreBoardService.getPlayerResults();

				Collections.sort(playerResults, new Comparator<PlayerResult>() {
					@Override
					public int compare(PlayerResult o1, PlayerResult o2) {
						int compare = 0;

						compare = new Double(o2.getPlayerRating()).compareTo(o1.getPlayerRating());
								
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
			protected void populateItem(ListItem<PlayerResult> item) {
				item.add(RowColorModifier.create(item));
				item.add(new Label("name", new PropertyModel<Integer>(item.getModel(), "player.name")));
				item.add(new Label("gamesCount", new PropertyModel<Integer>(item.getModel(), "gamesCount")));
				item.add(new Label("gamesWon", new PropertyModel<Integer>(item.getModel(), "gamesWon")));
				item.add(new Label("gamesLost", new PropertyModel<Integer>(item.getModel(), "gamesLost")));
				item.add(new Label("winRatio", new PropertyModel<Double>(item.getModel(), "gamesWonRatio")));
				item.add(new Label("rating", new PropertyModel<Double>(item.getModel(), "playerRating")));
			}
		});
	}

}
