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

package dk.frankbille.scoreboard.ratings;

import dk.frankbille.scoreboard.domain.*;
import dk.frankbille.scoreboard.test.WicketSpringTestCase;
import org.joda.time.DateMidnight;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

public abstract class RatingsTestCase extends WicketSpringTestCase {

	protected static League league = new League();
	protected static RatingCalculator rating;
	protected List<Player> players = new ArrayList<Player>();
	protected List<Long> games = new ArrayList<Long>();

	@Before
	public void setupTeams() {
		// League
		league.setName("Rating Test League");
		getScoreBoardService().saveLeague(league);

		// Players
		//Just get any 4 players
		players = getScoreBoardService().getAllPlayers().subList(0, 4);

		/*
		 * Games
		 */
		// Add 10 games with a winner team (player1+2) and a loser team (player3+4);
		DateMidnight date = new DateMidnight().minusDays(10);
		DateMidnight today = new DateMidnight();
		while (date.isBefore(today)) {
			Game game = createGame(date, players.get(0), players.get(1), players.get(2), players.get(3));
			getScoreBoardService().saveGame(game);
			games.add(game.getId());
			date = date.plusDays(1);
		}
		// Add a 11th game with a new winner team (player 4) and a new loser team (player 2)
		Game game = createGame(date, players.get(3), null, players.get(1), null);
		getScoreBoardService().saveGame(game);
	}

	protected static Game createGame(DateMidnight date, Player winnerTeamPlayer1, Player winnerTeam1Player2, Player winnerTeam2Player1, Player winnerTeam2Player2) {
		Game game = new Game();
		game.setDate(date.toDate());
		game.setTeam1(createGameTeam(10, game, "Team 1 " + date, winnerTeamPlayer1, winnerTeam1Player2));
		game.setTeam2(createGameTeam(0, game, "Team 2 " + date, winnerTeam2Player1, winnerTeam2Player2));
		game.setLeague(league);
		return game;
	}

	protected static GameTeam createGameTeam(int score, Game game, String name, Player player1, Player player2) {
		GameTeam gameTeam = new GameTeam();
		gameTeam.setScore(score);
		gameTeam.setGame(game);
		Team team = new Team();
		team.setName(name);
		team.addPlayer(player1);
		if (player2 != null) {
			team.addPlayer(player2);
		}
		gameTeam.setTeam(team);
		return gameTeam;
	}

	@Test
	public void testGeneralRating() {
		// Assert player scores after 10th game
		Long game = games.get(9);
		Long player1 = players.get(0).getId();
		Long player2 = players.get(1).getId();
		Long player3 = players.get(2).getId();
		Long player4 = players.get(3).getId();
		assertGreaterRating(game, player1, player3);
		assertGreaterRating(game, player2, player4);
		assertSameRating(game, player1, player2);
		assertSameRating(game, player3, player4);
		assertSameChange(game, player1, player2);
		assertSameChange(game, player3, player4);

		// Assert player scores after 11th game
		assertGreaterRating(player1, player2);
		assertGreaterRating(player4, player3);
		assertGreaterRating(player1, player3);
		assertGreaterRating(player2, player4);
	}

	public static void assertGreaterRating(Long game, Long player1, Long player2) {
		assertThat(rating.getGamePlayerRating(game, player1).getRating(), greaterThan(rating.getGamePlayerRating(game, player2).getRating()));
	}

	public static void assertGreaterChange(Long game, Long player1, Long player2) {
		assertThat(rating.getGamePlayerRating(game, player1).getChange(), greaterThan(rating.getGamePlayerRating(game, player2).getChange()));
	}

	public static void assertGreaterRating(Long player1, Long player2) {
		assertThat(rating.getPlayerRating(player1).getRating(), greaterThan(rating.getPlayerRating(player2).getRating()));
	}

	public static void assertSameRating(Long game, Long player1, Long player2) {
		assertThat(rating.getGamePlayerRating(game, player1).getRating(), equalTo(rating.getGamePlayerRating(game, player2).getRating()));
	}

	public static void assertSameChange(Long game, Long player1, Long player2) {
		assertThat(rating.getGamePlayerRating(game, player1).getChange(), equalTo(rating.getGamePlayerRating(game, player2).getChange()));
	}

}
