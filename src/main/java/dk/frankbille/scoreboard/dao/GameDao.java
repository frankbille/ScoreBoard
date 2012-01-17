package dk.frankbille.scoreboard.dao;

import java.util.List;

import dk.frankbille.scoreboard.domain.Game;

public interface GameDao {

	void saveGame(Game game);

	List<Game> getAllGames();

}
