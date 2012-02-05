package dk.frankbille.scoreboard.dao.mybatis;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;

import dk.frankbille.scoreboard.domain.GameTeam;

public interface GameTeamMapper {

	@Insert("INSERT INTO game_team (score, game_id, team_id) VALUES(#{score}, #{game.id}, #{team.id})")
	int insertGameTeam(GameTeam gameTeam);

	@Update("UPDATE game_team SET score=#{score} WHERE id=#{id}")
	void updateGameTeam(GameTeam gameTeam);

}
