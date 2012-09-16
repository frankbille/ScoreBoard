package dk.frankbille.scoreboard.dao.mybatis;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import dk.frankbille.scoreboard.dao.LeagueDao;
import dk.frankbille.scoreboard.domain.League;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class MyBatisLeagueDao implements LeagueDao {

	private LeagueMapper leagueMapper;

	@Autowired
	public MyBatisLeagueDao(LeagueMapper leagueMapper) {
		this.leagueMapper = leagueMapper;
	}

	@Override
	public void saveLeague(League league) {
		if (league.getId() == null) {
			leagueMapper.insertLeague(league);
		} else {
			leagueMapper.updateLeague(league);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public List<League> getAllLeagues() {
		return leagueMapper.getLeagues();
	}

	@Transactional(readOnly = true)
	@Override
	public League getLeague(Long leagueId) {
		return leagueMapper.getLeague(leagueId);
	}

}
