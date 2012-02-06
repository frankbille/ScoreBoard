package dk.frankbille.scoreboard.dao.mybatis;

import dk.frankbille.scoreboard.domain.GameTeam;

public interface GameTeamMapper {

	int insertGameTeam(GameTeam gameTeam);

	void updateGameTeam(GameTeam gameTeam);

}
