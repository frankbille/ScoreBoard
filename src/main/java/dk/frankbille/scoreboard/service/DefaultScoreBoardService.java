package dk.frankbille.scoreboard.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import dk.frankbille.scoreboard.dao.GameDao;
import dk.frankbille.scoreboard.dao.PlayerDao;
import dk.frankbille.scoreboard.domain.Game;
import dk.frankbille.scoreboard.domain.GameTeam;
import dk.frankbille.scoreboard.domain.Player;
import dk.frankbille.scoreboard.domain.PlayerResult;

@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class DefaultScoreBoardService implements ScoreBoardService {

	private GameDao gameDao;
	private PlayerDao playerDao;

	@Autowired
	public DefaultScoreBoardService(GameDao gameDao, PlayerDao playerDao) {
		this.gameDao = gameDao;
		this.playerDao = playerDao;
	}

	@Override
	public Player createNewPlayer(String name) {
		Player player = new Player();
		player.setName(name);
		playerDao.savePlayer(player);
		return player;
	}

	@Override
	public void savePlayer(Player player) {
		playerDao.savePlayer(player);
	}

	@Transactional(readOnly=true)
	@Override
	public List<Player> getAllPlayers() {
		return playerDao.getAllPlayers();
	}

	@Override
	public void saveGame(Game game) {
		gameDao.saveGame(game);
	}

	@Override
	public List<Game> getAllGames() {
		return gameDao.getAllGames();
	}

	@Override
	public List<PlayerResult> getPlayerResults() {
		List<PlayerResult> playerResults = new ArrayList<PlayerResult>();

		Map<Player, PlayerResult> cache = new HashMap<Player, PlayerResult>();

		List<Game> games = gameDao.getAllGames();
		for (Game game : games) {
			List<GameTeam> gameTeams = game.getTeams();
			for (GameTeam gameTeam : gameTeams) {
				Set<Player> players = gameTeam.getTeam().getPlayers();
				for (Player player : players) {
					PlayerResult result = cache.get(player);
					if (result == null) {
						result = new PlayerResult(player);
						cache.put(player, result);
						playerResults.add(result);
					}

					if (game.didTeamWin(gameTeam)) {
						result.gameWon();
					} else {
						result.gameLost();
					}
				}
			}
		}

		return playerResults;
	}

	@Override
	public Player getPlayer(Long playerId) {
		return playerDao.getPlayer(playerId);
	}

}
