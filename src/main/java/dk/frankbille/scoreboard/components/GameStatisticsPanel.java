package dk.frankbille.scoreboard.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import dk.frankbille.scoreboard.comparators.PlayerComparator;
import dk.frankbille.scoreboard.domain.Game;
import dk.frankbille.scoreboard.domain.Player;
import dk.frankbille.scoreboard.ratings.GamePlayerRating;
import dk.frankbille.scoreboard.ratings.RatingCalculator;
import dk.frankbille.scoreboard.ratings.RatingProvider;
import dk.frankbille.scoreboard.service.ScoreBoardService;

public class GameStatisticsPanel extends Panel {
	private static final long serialVersionUID = 1L;

	@SpringBean
	private ScoreBoardService scoreBoardService;
	private final IModel<List<Game>> gameModel;
	private final WebComponent chart;

	public GameStatisticsPanel(String id, IModel<List<Game>> gameModel) {
		super(id);
		this.gameModel = gameModel;

		chart = new WebComponent("chart");
		chart.setOutputMarkupId(true);
		add(chart);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		StringBuilder javascript = new StringBuilder();
		javascript.append("google.load('visualization', '1', {packages:['corechart']});\n");
		javascript.append("google.setOnLoadCallback(drawChart);\n");
		javascript.append("function drawChart() {\n");
		javascript.append("var data = google.visualization.arrayToDataTable([\n");

		List<Game> games = new ArrayList<Game>(gameModel.getObject());

		Collections.sort(games, new Comparator<Game>() {
			@Override
			public int compare(Game o1, Game o2) {
				return o1.getId().compareTo(o2.getId());
			}
		});

		Set<Player> players = new HashSet<Player>();
		for (Game game : games) {
			players.addAll(game.getTeam1().getTeam().getPlayers());
			players.addAll(game.getTeam2().getTeam().getPlayers());
		}
		List<Player> sortedPlayers = new ArrayList<Player>(players);
		Collections.sort(sortedPlayers, new PlayerComparator());

		javascript.append("['Game'");
		for (Player player : sortedPlayers) {
			javascript.append(", '").append(player.getName()).append("'");
		}
		javascript.append("]\n");

		// Game zero
		int gameIndex = 0;

		javascript.append(", ['").append(gameIndex++).append("'");
		for (int i = 0; i < sortedPlayers.size(); i++) {
			javascript.append(", 1000");
		}
		javascript.append("]\n");

		RatingCalculator rating = RatingProvider.getRatings();

		for (Game game : games) {
			javascript.append(", ['").append(gameIndex++).append("'");
			Set<Player> gamePlayers = new HashSet<Player>();
			gamePlayers.addAll(game.getTeam1().getTeam().getPlayers());
			gamePlayers.addAll(game.getTeam2().getTeam().getPlayers());

			for (Player player : sortedPlayers) {
				javascript.append(", ");
				if (gamePlayers.contains(player)) {
					GamePlayerRating gamePlayerRating = rating.getGamePlayerRating(game.getId(), player.getId());
					javascript.append(Math.round(gamePlayerRating.getRating()+gamePlayerRating.getChange()));
				} else {
					javascript.append("null");
				}
			}

			javascript.append("]\n");
		}
        javascript.append("]);\n");

        javascript.append("var options = {\n");
        javascript.append("interpolateNulls : true\n");
        javascript.append("};\n");

		javascript.append("var chart = new google.visualization.LineChart(document.getElementById('").append(chart.getMarkupId()).append("'));\n");
		javascript.append("chart.draw(data, options);\n");
		javascript.append("}\n");
		response.renderJavaScript(javascript, "gamechartscript");
	}

}
