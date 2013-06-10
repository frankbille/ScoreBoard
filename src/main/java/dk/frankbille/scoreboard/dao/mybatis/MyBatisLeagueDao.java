/*
 * ScoreBoard
 * Copyright (C) 2012-2013 Frank Bille
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
