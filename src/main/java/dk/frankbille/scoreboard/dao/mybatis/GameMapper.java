package dk.frankbille.scoreboard.dao.mybatis;

import java.util.List;

import dk.frankbille.scoreboard.domain.Game;
import dk.frankbille.scoreboard.domain.League;

public interface GameMapper {

	int insertGame(Game game);

	void updateGame(Game game);

	List<Game> getAllGames();

	List<Game> getAllGamesByLeague(League league);

	Game getGame(Long gameId);

}
