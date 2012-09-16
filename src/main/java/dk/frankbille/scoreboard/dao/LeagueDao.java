package dk.frankbille.scoreboard.dao;

import java.util.List;

import dk.frankbille.scoreboard.domain.League;

public interface LeagueDao {

	List<League> getAllLeagues();
	
	League getLeague(Long leagueId);

	void saveLeague(League league);
	
}
