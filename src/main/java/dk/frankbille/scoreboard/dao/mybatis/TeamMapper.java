package dk.frankbille.scoreboard.dao.mybatis;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import dk.frankbille.scoreboard.domain.Player;
import dk.frankbille.scoreboard.domain.Team;

public interface TeamMapper {

	@Insert("INSERT INTO team (name) VALUES(#{name})")
	int insertTeam(Team team);

	@Update("UPDATE team SET name=#{name} WHERE id=#{id}")
	void updateTeam(Team team);

	@Insert("INSERT INTO team_players (team_id, player_id) VALUES(#{team.id}, #{player.id})")
	void insertTeamPlayer(@Param("team") Team team, @Param("player") Player player);

	@Delete("DELETE FROM team_players WHERE team_id=#{team_id}")
	void deleteTeamPlayers(Team team);

}
