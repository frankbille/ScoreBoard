package dk.frankbille.scoreboard.dao.mybatis;

import java.util.List;

import dk.frankbille.scoreboard.domain.Game;

public interface GameMapper {

	int insertGame(Game game);

	void updateGame(Game game);

	List<Game> getAllGames();

}
