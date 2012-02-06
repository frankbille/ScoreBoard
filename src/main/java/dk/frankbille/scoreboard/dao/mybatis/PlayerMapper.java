package dk.frankbille.scoreboard.dao.mybatis;

import java.util.List;

import dk.frankbille.scoreboard.domain.Player;

public interface PlayerMapper {

	int insertPlayer(Player player);

	void updatePlayer(Player player);

	List<Player> getPlayers();

}
