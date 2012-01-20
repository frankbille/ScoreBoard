package dk.frankbille.scoreboard.service;

import java.util.List;

import dk.frankbille.scoreboard.domain.Game;
import dk.frankbille.scoreboard.domain.Player;
import dk.frankbille.scoreboard.domain.PlayerResult;

public interface ScoreBoardService {

	Player createNewPlayer(String name);

	List<Player> getAllPlayers();

	void saveGame(Game game);

	List<Game> getAllGames();

	List<PlayerResult> getPlayerResults();

}
