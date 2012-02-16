package dk.frankbille.scoreboard.dao.mybatis;

import org.apache.ibatis.annotations.Param;

import dk.frankbille.scoreboard.domain.Player;
import dk.frankbille.scoreboard.domain.Team;

public interface TeamMapper {

	int insertTeam(Team team);

	void updateTeam(Team team);

	void insertTeamPlayer(@Param("team") Team team, @Param("player") Player player);

	void deleteTeamPlayers(Team team);

}
