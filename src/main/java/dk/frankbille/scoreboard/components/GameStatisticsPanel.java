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

package dk.frankbille.scoreboard.components;

import dk.frankbille.scoreboard.comparators.PlayerComparator;
import dk.frankbille.scoreboard.domain.Game;
import dk.frankbille.scoreboard.domain.Player;
import dk.frankbille.scoreboard.ratings.GamePlayerRatingInterface;
import dk.frankbille.scoreboard.ratings.RatingCalculator;
import dk.frankbille.scoreboard.service.ScoreBoardService;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.*;

public class GameStatisticsPanel extends Panel {
    private static final long serialVersionUID = 1L;
    private final IModel<List<Game>> gameModel;
    private final WebComponent chart;
    private final RatingCalculator rating;
    @SpringBean
    private ScoreBoardService scoreBoardService;

    public GameStatisticsPanel(String id, IModel<List<Game>> gameModel, RatingCalculator rating) {
        super(id);
        this.gameModel = gameModel;
        this.rating = rating;

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
                final int dateComparison = o1.getDate().compareTo(o2.getDate());
                return dateComparison == 0 ? o1.getId().compareTo(o2.getId()) : dateComparison;
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
            javascript.append(", ");
            javascript.append(rating.getDefaultRating());
        }
        javascript.append("]\n");


        for (Game game : games) {
            javascript.append(", ['").append(gameIndex++).append("'");
            Set<Player> gamePlayers = new HashSet<Player>();
            gamePlayers.addAll(game.getTeam1().getTeam().getPlayers());
            gamePlayers.addAll(game.getTeam2().getTeam().getPlayers());

            for (Player player : sortedPlayers) {
                javascript.append(", ");
                if (gamePlayers.contains(player)) {
                    GamePlayerRatingInterface gamePlayerRating = rating.getGamePlayerRating(game.getId(), player.getId());
                    javascript.append(Math.round(gamePlayerRating.getRating() + gamePlayerRating.getChange()));
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
        response.render(JavaScriptHeaderItem.forScript(javascript, "gamechartscript"));
    }

}
