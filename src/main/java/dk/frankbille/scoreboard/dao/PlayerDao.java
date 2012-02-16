package dk.frankbille.scoreboard.dao;

import java.util.List;

import dk.frankbille.scoreboard.domain.Player;

public interface PlayerDao {

	void savePlayer(Player player);

	List<Player> getAllPlayers();

	Player getPlayer(Long playerId);

}
