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

import dk.frankbille.scoreboard.ratings.RatingInterface;
import jskills.Rating;

public class TrueSkillRating extends Rating implements RatingInterface {

    public TrueSkillRating(double mean, double standardDeviation) {
        super(mean, standardDeviation);
    }

    public TrueSkillRating(double mean, double standardDeviation, double conservativeStandardDeviationMultiplier) {
        super(mean, standardDeviation, conservativeStandardDeviationMultiplier);
    }

    @Override
    public double getRating() {
        return this.getMean();
    }

    @Override
    public void changeRating(double ratingChange) {

    }
}
