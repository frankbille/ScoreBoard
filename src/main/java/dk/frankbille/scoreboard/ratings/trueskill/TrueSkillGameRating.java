/*
 * ScoreBoard
 * Copyright (C) 2012-2015 Frank Bille
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

package dk.frankbille.scoreboard.ratings.trueskill;

import dk.frankbille.scoreboard.ratings.GameRatingInterface;

public class TrueSkillGameRating implements GameRatingInterface {
    private double winnerRating;
    private long winnerTeamId;
    private double loserRating;
    private long loserTeamId;
    private double winnerChange;
    private double loserChange;

    public TrueSkillGameRating(long winnerTeamId, double winnerRating, double winnerChange, long loserTeamId, double loserRating, double loserChange) {
        this.winnerRating = winnerRating;
        this.winnerTeamId = winnerTeamId;
        this.loserRating = loserRating;
        this.loserTeamId = loserTeamId;
        this.winnerChange = winnerChange;
        this.loserChange = loserChange;
    }

    @Override
    public double getChange(long teamId) {
        if (winnerTeamId==teamId) {
            return winnerChange;
        } else if (loserTeamId == teamId) {
            return loserChange;
        } else {
            throw new IllegalArgumentException(String.format("Team id %d not found in rating", teamId));
        }
    }

    @Override
    public double getRating(long teamId) {
        if (winnerTeamId == teamId) {
            return winnerRating;
        } else if (loserTeamId == teamId) {
            return loserRating;
        } else {
            throw new IllegalArgumentException(String.format("Team id %d not found in rating", teamId));
        }
    }
}
