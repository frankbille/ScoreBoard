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

package dk.frankbille.scoreboard.ratings;

import dk.frankbille.scoreboard.ScoreBoardSession;
import dk.frankbille.scoreboard.domain.RatingCalculatorType;
import dk.frankbille.scoreboard.domain.User;
import dk.frankbille.scoreboard.ratings.elo.ELORatingCalculator;
import dk.frankbille.scoreboard.ratings.trueskill.TrueSkillRatingCalculator;

public class RatingProvider {
	public static RatingCalculator ratings;
	public static RatingCalculator getRatings() {
		RatingCalculatorType ratingCalculator = RatingCalculatorType.ELO;
		if (ScoreBoardSession.get().isAuthenticated()) {
			User user = ScoreBoardSession.get().getUser();
			ratingCalculator = user.getRatingCalculator();
		}
		if (ratings==null || ratings.getType() != ratingCalculator) {
			if (ratingCalculator == RatingCalculatorType.TRUESKILL){
				ratings = new TrueSkillRatingCalculator();
			} else {
				ratings = new ELORatingCalculator();
			}
		}
		return ratings;
	}
}
