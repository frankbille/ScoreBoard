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

package dk.frankbille.scoreboard.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class TeamResult implements Serializable {
	private static final long serialVersionUID = 1L;

	private TeamId team;

	private int gamesWon = 0;

	private int gamesLost = 0;

	private Trend trend;

	private String name;

	public TeamResult(TeamId team, Set<Player> players) {
		this.team = team;
		List<String> names = new ArrayList<String>();
		for (Player player : players) {
			names.add(player.getName());
		}
		Collections.sort(names);
		this.name = StringUtils.join(names, ", ");
	}

	public TeamId getTeam() {
		return team;
	}

	public void gameWon() {
		gamesWon++;
	}

	public void gameLost() {
		gamesLost++;
	}

	public int getGamesCount() {
		return gamesWon+gamesLost;
	}

	public int getGamesWon() {
		return gamesWon;
	}

	public int getGamesLost() {
		return gamesLost;
	}

	public double getGamesWonRatio() {
		return gamesWon+gamesLost > 0 ? (double) gamesWon / ((double)gamesWon+(double)gamesLost) : 0.0;
	}

	public Trend getTrend() {
		return trend;
	}

	public void setTrend(Trend trend) {
		this.trend = trend;
	}

	public String getName() {
		return name;
	}
}
