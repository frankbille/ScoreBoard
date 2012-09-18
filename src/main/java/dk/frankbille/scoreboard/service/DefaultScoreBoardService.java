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
import dk.frankbille.scoreboard.dao.LeagueDao;
import dk.frankbille.scoreboard.dao.PlayerDao;
import dk.frankbille.scoreboard.dao.UserDao;
import dk.frankbille.scoreboard.domain.Game;
import dk.frankbille.scoreboard.domain.GameTeam;
import dk.frankbille.scoreboard.domain.League;
import dk.frankbille.scoreboard.domain.Player;
import dk.frankbille.scoreboard.domain.PlayerResult;
import dk.frankbille.scoreboard.domain.PlayerResult.Trend;
import dk.frankbille.scoreboard.domain.User;
import dk.frankbille.scoreboard.ratings.RatingCalculator;
import dk.frankbille.scoreboard.ratings.RatingProvider;

@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class DefaultScoreBoardService implements ScoreBoardService {

	private final GameDao gameDao;
	private final PlayerDao playerDao;
	private final UserDao userDao;
	private final LeagueDao leagueDao;

	@Autowired
	public DefaultScoreBoardService(GameDao gameDao, PlayerDao playerDao, UserDao userDao, LeagueDao leagueDao) {
		this.gameDao = gameDao;
		this.playerDao = playerDao;
		this.userDao = userDao;
		this.leagueDao = leagueDao;
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
	
	@Transactional(readOnly = true)
	@Override
	public Game getGame(Long gameId) {
		return gameDao.getGame(gameId);
	}

	@Transactional(readOnly = true)
	@Override
	public List<Game> getAllGames() {
		List<Game> games = gameDao.getAllGames();
		RatingCalculator rating = RatingProvider.getRatings();
		rating.setGames(games);
		return games;
	}

	@Transactional(readOnly = true)
	@Override
	public List<Game> getAllGames(League league) {
		List<Game> games = gameDao.getAllGames(league);
		RatingCalculator rating = RatingProvider.getRatings();
		rating.setGames(games);
		return games;
	}

	@Transactional(readOnly = true)
	@Override
	public List<Game> getAllGames(League league, int numberOfGames) {
		List<Game> allGames = getAllGames(league);
		numberOfGames = Math.min(numberOfGames, allGames.size());
		return allGames.subList(0, numberOfGames-1);
	}

	@Transactional(readOnly = true)
	@Override
	public List<Game> getPlayerGames(Player player) {
		List<Game> playerGames = new ArrayList<Game>();

		List<Game> allGames = getAllGames();
		for (Game game : allGames) {
			if (game.hasPlayer(player)) {
				playerGames.add(game);
			}
		}

		return playerGames;
	}

	@Transactional(readOnly = true)
	@Override
	public List<PlayerResult> getPlayerResults() {
		return getPlayerResults(null);
	}

	@Transactional(readOnly = true)
	@Override
	public List<PlayerResult> getPlayerResults(League league) {
		List<PlayerResult> playerResults = new ArrayList<PlayerResult>();
		Map<Player, PlayerResult> cache = new HashMap<Player, PlayerResult>();
		Map<Player, List<Game>> playerGamesCache = new HashMap<Player, List<Game>>();

		List<Game> games;
		if (league != null) {
			games = gameDao.getAllGames(league);
		} else {
			games = gameDao.getAllGames();
		}
		for (Game game : games) {
			extractPlayerStatistics(game.getTeam1(), game, playerResults, cache, playerGamesCache);
			extractPlayerStatistics(game.getTeam2(), game, playerResults, cache, playerGamesCache);
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
						int compare = o2.getDate().compareTo(o1.getDate());
						if (compare == 0) {
							compare = o2.getId().compareTo(o1.getId());
						}
						return compare;
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

	private void extractPlayerStatistics(GameTeam gameTeam, Game game,
			List<PlayerResult> playerResults, Map<Player, PlayerResult> cache,
			Map<Player, List<Game>> playerGamesCache) {
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

	@Transactional(readOnly = true)
	@Override
	public Player getPlayer(Long playerId) {
		return playerDao.getPlayer(playerId);
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<Player> searchPlayers(String term) {
		return playerDao.searchPlayers(term);
	}

	@Transactional(readOnly = true)
	@Override
	public User authenticate(String username, String password) {
		return userDao.authenticate(username, password);
	}

	@Override
	public void createUser(User user, String password) {
		userDao.createUser(user, password);
	}

	@Override
	public void updateUser(User user) {
		userDao.updateUser(user);
	}

	@Transactional(readOnly = true)
	@Override
	public User getUserForPlayer(Player player) {
		return userDao.getUserForPlayer(player);
	}

	@Transactional(readOnly = true)
	@Override
	public boolean hasUserWithUsername(String username) {
		return userDao.hasUserWithUsername(username);
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<League> getAllLeagues() {
		return leagueDao.getAllLeagues();
	}

	@Transactional(readOnly = true)
	@Override
	public League getLeague(Long leagueId) {
		return leagueDao.getLeague(leagueId);
	}
	
	@Override
	public void saveLeague(League league) {
		leagueDao.saveLeague(league);
	}

}
