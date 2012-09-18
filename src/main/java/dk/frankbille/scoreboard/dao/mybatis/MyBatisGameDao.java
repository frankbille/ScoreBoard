package dk.frankbille.scoreboard.dao.mybatis;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import dk.frankbille.scoreboard.comparators.GameComparator;
import dk.frankbille.scoreboard.dao.GameDao;
import dk.frankbille.scoreboard.domain.Game;
import dk.frankbille.scoreboard.domain.GameTeam;
import dk.frankbille.scoreboard.domain.League;
import dk.frankbille.scoreboard.domain.Player;
import dk.frankbille.scoreboard.domain.Team;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class MyBatisGameDao implements GameDao {

	private final GameMapper gameMapper;
	private final GameTeamMapper gameTeamMapper;
	private final TeamMapper teamMapper;

	@Autowired
	public MyBatisGameDao(GameMapper gameMapper, GameTeamMapper gameTeamMapper,
			TeamMapper teamMapper) {
		this.gameMapper = gameMapper;
		this.gameTeamMapper = gameTeamMapper;
		this.teamMapper = teamMapper;
	}

	@Override
	public void saveGame(Game game) {
		saveGameTeam(game.getTeam1());
		saveGameTeam(game.getTeam2());

		if (game.getId() == null) {
			gameMapper.insertGame(game);
		} else {
			gameMapper.updateGame(game);
		}
	}

	private void saveGameTeam(GameTeam gameTeam) {
		Team team = gameTeam.getTeam();
		if (team != null) {
			if (team.getId() == null) {
				teamMapper.insertTeam(team);
			} else {
				teamMapper.updateTeam(team);
			}

			teamMapper.deleteTeamPlayers(team);

			Set<Player> players = team.getPlayers();
			if (players != null) {
				for (Player player : players) {
					teamMapper.insertTeamPlayer(team, player);
				}
			}
		}

		if (gameTeam.getId() == null) {
			gameTeamMapper.insertGameTeam(gameTeam);
		} else {
			gameTeamMapper.updateGameTeam(gameTeam);
		}
	}

	@Override
	public List<Game> getAllGames() {
		List<Game> allGames = gameMapper.getAllGames();
		/*
		 * The MyBatis mapper can currently not resolve circular references
		 * (Game->GameTeam->Game), so we have to do it manually.
		 */
		for (Game game : allGames) {
			game.getTeam1().setGame(game);
			game.getTeam2().setGame(game);
		}
		Collections.sort(allGames, new GameComparator());
		return allGames;
	}
	
	@Override
	public List<Game> getAllGames(League league) {
		List<Game> allGames = gameMapper.getAllGamesByLeague(league);
		/*
		 * The MyBatis mapper can currently not resolve circular references
		 * (Game->GameTeam->Game), so we have to do it manually.
		 */
		for (Game game : allGames) {
			game.getTeam1().setGame(game);
			game.getTeam2().setGame(game);
		}
		Collections.sort(allGames, new GameComparator());
		return allGames;
	}
	
	@Override
	public Game getGame(Long gameId) {
		return gameMapper.getGame(gameId);
	}

}
