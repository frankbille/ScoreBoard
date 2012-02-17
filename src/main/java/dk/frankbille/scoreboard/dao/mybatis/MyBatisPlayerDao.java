package dk.frankbille.scoreboard.dao.mybatis;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import dk.frankbille.scoreboard.dao.PlayerDao;
import dk.frankbille.scoreboard.domain.Player;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class MyBatisPlayerDao implements PlayerDao {

	private PlayerMapper playerMapper;

	@Autowired
	public MyBatisPlayerDao(PlayerMapper playerMapper) {
		this.playerMapper = playerMapper;
	}

	@Override
	public void savePlayer(Player player) {
		if (player.getId() == null) {
			playerMapper.insertPlayer(player);
		} else {
			playerMapper.updatePlayer(player);
		}
	}

	@Override
	public List<Player> getAllPlayers() {
		return playerMapper.getPlayers();
	}

	@Override
	public Player getPlayer(Long playerId) {
		return playerMapper.getPlayer(playerId);
	}

}
