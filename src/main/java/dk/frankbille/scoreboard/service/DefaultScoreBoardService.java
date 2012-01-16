package dk.frankbille.scoreboard.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import dk.frankbille.scoreboard.dao.GameDao;
import dk.frankbille.scoreboard.dao.PlayerDao;
import dk.frankbille.scoreboard.domain.Player;

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
	public List<Player> getAllPlayers() {
		return playerDao.getAllPlayers();
	}

}
