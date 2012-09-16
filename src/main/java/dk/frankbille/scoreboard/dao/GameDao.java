package dk.frankbille.scoreboard.dao;

import java.util.List;

import dk.frankbille.scoreboard.domain.Game;
import dk.frankbille.scoreboard.domain.League;

public interface GameDao {

	void saveGame(Game game);

	List<Game> getAllGames();

	List<Game> getAllGames(League league);

	Game getGame(Long gameId);

}
