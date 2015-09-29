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

import dk.frankbille.scoreboard.domain.Game;
import dk.frankbille.scoreboard.domain.Player;
import dk.frankbille.scoreboard.ratings.RatingsTestCase;
import org.joda.time.DateMidnight;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class TestTrueSkillRating extends RatingsTestCase {

	@Before
	public void setupRating() {
		rating = new TrueSkillRatingCalculator(getScoreBoardService().getAllGames(league));
	}

	@Test
	public void testDifferentiatedRatingForPlayerOnSameTeam() {
		Player player1 = players.get(0);
		Player player2 = players.get(1);
		Player player3 = players.get(2);
		Player player4 = players.get(3);

		// Add a 12th game with a new winner team (player 1 and 3) and a new loser team (player 2 and 4)
		DateMidnight date = new DateMidnight().plusDays(1);
		Game game = createGame(date, player1, player2, player3, player4);
		getScoreBoardService().saveGame(game);

		List<Game> games = getScoreBoardService().getAllGames(league);
		rating = new TrueSkillRatingCalculator(games);
		assertGreaterChange(game.getId(), player1.getId(), player2.getId()); // Player 1 had the greatest victory
		assertGreaterChange(game.getId(), player4.getId(), player3.getId()); // Player 3 had the greatest loss
	}

}
