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

import dk.frankbille.scoreboard.ratings.GamePlayerRatingInterface;

public class TrueSkillPlayerRating implements GamePlayerRatingInterface {
    private double rating;
    private double change;

    public TrueSkillPlayerRating(double rating, double change) {
        this.rating = rating;
        this.change = change;
    }

    @Override
    public double getRating() {
        return rating;
    }

    @Override
    public double getChange() {
        return change;
    }
}

