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

package dk.frankbille.scoreboard.ratings.elo;

import dk.frankbille.scoreboard.ratings.GameRatingInterface;

public class ELOGameRating implements GameRatingInterface {
	private double winnerRating;
	private long winnerTeamId;
	private double loserRating;
	private long loserTeamId;
	private double change;

	public ELOGameRating(long winnerTeamId, double winnerRating, long loserTeamId, double loserRating, double change) {
		this.winnerTeamId = winnerTeamId;
		this.winnerRating = winnerRating;
		this.loserTeamId = loserTeamId;
		this.loserRating = loserRating;
		this.change = change;
	}

	@Override
	public double getChange(long teamId) {
		if (winnerTeamId==teamId) {
			return +change;
		}
		else if (loserTeamId==teamId) {
			return -change;
		}
		else {
			throw new IllegalArgumentException(String.format("Team id %d not found in rating", teamId));
		}
	}

	@Override
	public double getRating(long teamId) {
		if (winnerTeamId==teamId) {
			return winnerRating;
		}
		else if (loserTeamId==teamId) {
			return loserRating;
		}
		else {
			throw new IllegalArgumentException(String.format("Team id %d not found in rating", teamId));
		}
	}
}
