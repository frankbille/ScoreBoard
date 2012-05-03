package dk.frankbille.scoreboard.domain;

import java.io.Serializable;

import dk.frankbille.scoreboard.ratings.RatingProvider;

public class PlayerResult implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static enum Trend {
		WINNING,
		LOOSING,
		EVEN,
		NOT_DEFINED
	}

	private Player player;
	
	private int gamesWon = 0;

	private int gamesLost = 0;
	
	private Trend trend;

	public PlayerResult(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	public void gameWon() {
		gamesWon++;
	}

	public void gameLost() {
		gamesLost++;
	}

	public int getGamesCount() {
		return gamesWon+gamesLost;
	}

	public int getGamesWon() {
		return gamesWon;
	}

	public int getGamesLost() {
		return gamesLost;
	}

	public double getGamesWonRatio() {
		return gamesWon+gamesLost > 0 ? (double) gamesWon / ((double)gamesWon+(double)gamesLost) : 0.0;
	}

	public double getRating() {
		return RatingProvider.getRatings().getPlayerRating(player.getId()).getRating();
	}
	
	public Trend getTrend() {
		return trend;
	}
	
	public void setTrend(Trend trend) {
		this.trend = trend;
	}

}
