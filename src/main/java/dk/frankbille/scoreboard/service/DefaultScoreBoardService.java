package dk.frankbille.scoreboard.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import dk.frankbille.scoreboard.domain.PlayerResult.Trend;
import dk.frankbille.scoreboard.ratings.RatingCalculator;
import dk.frankbille.scoreboard.ratings.RatingProvider;

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
		List<Game> games = gameDao.getAllGames();
		RatingCalculator rating = RatingProvider.getRatings();
		rating.setGames(games);
		return games;
	}

	@Override
	public List<Game> getPlayerGames(Player player) {
		List<Game> playerGames = new ArrayList<Game>();

		List<Game> allGames = getAllGames();
		for (Game game : allGames) {
			List<GameTeam> teams = game.getTeams();
			for (GameTeam gameTeam : teams) {
				Set<Player> players = gameTeam.getTeam().getPlayers();
				for (Player p : players) {
					if (player.equals(p)) {
						playerGames.add(game);
						break;
					}
				}
			}
		}

		return playerGames;
	}

	@Override
	public List<PlayerResult> getPlayerResults() {
		List<PlayerResult> playerResults = new ArrayList<PlayerResult>();
		Map<Player, PlayerResult> cache = new HashMap<Player, PlayerResult>();
		Map<Player, List<Game>> playerGamesCache = new HashMap<Player, List<Game>>();

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
					
					List<Game> playerGames = playerGamesCache.get(player);
					if (playerGames == null) {
						playerGames = new ArrayList<Game>();
						playerGamesCache.put(player, playerGames);
					}
					playerGames.add(game);

					if (game.didTeamWin(gameTeam)) {
						result.gameWon();
					} else {
						result.gameLost();
					}
				}
			}
		}
		
		// Add trends
		for (Player player : playerGamesCache.keySet()) {
			List<Game> playerGames = playerGamesCache.get(player);
			// Take the last 3 games
			int trendPeriod = playerGames.size() < 3 ? playerGames.size() : 3;
			if (playerGames.size() > 0) {
				Collections.sort(playerGames, new Comparator<Game>() {
					@Override
					public int compare(Game o1, Game o2) {
						return o2.getDate().compareTo(o1.getDate());
					}
				});
			
				int winCount = 0;
				int looseCount = 0;
				for (int i = 0; i < trendPeriod; i++) {
					Game game = playerGames.get(i);
					if (game.didPlayerWin(player)) {
						winCount++;
					} else {
						looseCount++;
					}
				}
				
				if (winCount > looseCount) {
					cache.get(player).setTrend(Trend.WINNING);
				} else if (winCount < looseCount) {
					cache.get(player).setTrend(Trend.LOOSING);
				} else {
					cache.get(player).setTrend(Trend.EVEN);
				}
			} else {
				cache.get(player).setTrend(Trend.NOT_DEFINED);
			}
		}

		return playerResults;
	}

	@Override
	public Player getPlayer(Long playerId) {
		return playerDao.getPlayer(playerId);
	}

}
