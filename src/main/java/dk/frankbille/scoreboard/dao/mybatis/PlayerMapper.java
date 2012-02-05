package dk.frankbille.scoreboard.dao.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import dk.frankbille.scoreboard.domain.Player;

public interface PlayerMapper {

	@Insert("INSERT INTO player (name) VALUES (#{name})")
	int insertPlayer(Player player);

	@Update("UPDATE player SET name=#{name} WHERE id=#{id}")
	void updatePlayer(Player player);

	@Select("SELECT	* FROM player ORDER BY name")
	List<Player> getPlayers();

}
