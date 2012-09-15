package dk.frankbille.scoreboard.dao.mybatis;

import java.util.List;

import dk.frankbille.scoreboard.domain.League;

public interface LeagueMapper {

	int insertLeague(League league);

	void updateLeague(League league);

	List<League> getLeagues();

	League getLeague(Long leagueId);

}
