package dk.frankbille.scoreboard.daily;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.wicket.model.AbstractReadOnlyModel;

import dk.frankbille.scoreboard.comparators.PlayerComparator;
import dk.frankbille.scoreboard.domain.Game;
import dk.frankbille.scoreboard.domain.GameTeam;
import dk.frankbille.scoreboard.domain.Player;
import dk.frankbille.scoreboard.ratings.GamePlayerRating;
import dk.frankbille.scoreboard.ratings.GameRating;
import dk.frankbille.scoreboard.ratings.RatingCalculator;
import dk.frankbille.scoreboard.ratings.RatingProvider;

public class TeamReadOnlyModel extends AbstractReadOnlyModel<String> {
	private static final long serialVersionUID = 1L;
	private GameTeam team;
	private Game game;

	public TeamReadOnlyModel(Game game, GameTeam team) {
		this.game = game;
		this.team = team;
	}

	@Override
	public String getObject() {
		RatingCalculator rating = RatingProvider.getRatings();
		StringBuilder b = new StringBuilder();
		DecimalFormat ratingDf = new DecimalFormat("#,##0");
		DecimalFormat changeDf = new DecimalFormat("+0.0;-0.0");
		GamePlayerRating playerRating;

		List<Player> players = new ArrayList<Player>(team.getTeam().getPlayers());
		Collections.sort(players, new PlayerComparator());
		for (Player player : players) {
			if (b.length() > 0) {
				b.append("\n");
			}
			b.append(player.getName());
			playerRating= rating.getGamePlayerRating(game.getId(),player.getId());
			b.append(" (");
			b.append(ratingDf.format(playerRating.getRating()));
			b.append(")");
		}
		
		//Add the team rating
		GameRating gameRating = rating.getGameRatingChange(game.getId());
		b.append("\nTeam: ");
		if (game.didTeamWin(team))
			b.append(ratingDf.format(gameRating.getWinnerRating()));
		else
			b.append(ratingDf.format(gameRating.getLoserRating()));
		
		//Add the team rating change
		double change = game.didTeamWin(team) ? gameRating.getChange() : -gameRating.getChange();
		if (players.size()>0)
			change /= players.size();
		b.append(" ");
		b.append(changeDf.format(change));

		return b.toString();
	}
}
