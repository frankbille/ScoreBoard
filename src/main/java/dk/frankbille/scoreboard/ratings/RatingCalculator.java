package dk.frankbille.scoreboard.ratings;

import java.util.List;

import dk.frankbille.scoreboard.domain.Game;

public interface RatingCalculator {
	public PlayerRating getPlayerRating(long playerId);
	public GamePlayerRating getGamePlayerRating(long gameId, long playerId);
	public void setGames(List<Game> games);
	public GameRating getGameRatingChange(Long id);
}
