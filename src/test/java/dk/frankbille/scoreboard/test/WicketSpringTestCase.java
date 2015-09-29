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

package dk.frankbille.scoreboard.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.mock.MockServletContext;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.util.tester.WicketTester;
import org.joda.time.DateMidnight;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import dk.frankbille.scoreboard.ScoreBoardApplication;
import dk.frankbille.scoreboard.ScoreBoardSession;
import dk.frankbille.scoreboard.dao.mybatis.TestMapper;
import dk.frankbille.scoreboard.domain.Game;
import dk.frankbille.scoreboard.domain.GameTeam;
import dk.frankbille.scoreboard.domain.League;
import dk.frankbille.scoreboard.domain.Player;
import dk.frankbille.scoreboard.domain.Team;
import dk.frankbille.scoreboard.domain.User;
import dk.frankbille.scoreboard.service.ScoreBoardService;

public abstract class WicketSpringTestCase {

	private static ServletContext servletContext;
	protected WicketTester tester;
	protected static WebApplicationContext applicationContext;

	@BeforeClass
	public static void setupSpring() {
		if (applicationContext == null) {
			MockServletContext servletContext = new MockServletContext(new ScoreBoardApplication(), "src/main/webapp");
			servletContext.addInitParameter("contextConfigLocation", "classpath:applicationContext-test.xml");
			ContextLoader loader = new ContextLoader();
			applicationContext = loader.initWebApplicationContext(servletContext);
			WicketSpringTestCase.servletContext = servletContext;
		}
	}

	@Before
	public void setupWicket() {
		insertTestData();

		ScoreBoardApplication application = new ScoreBoardApplication() {
			@Override
			public Session newSession(Request request, Response response) {
				ScoreBoardSession session = (ScoreBoardSession) super.newSession(request, response);
				session.authenticate("username1", "password1");
				return session;
			}
		};
		tester = new WicketTester(application, servletContext);
	}

	private void insertTestData() {
		/*
		 * Leagues
		 */
		List<League> leagues = new ArrayList<League>();
		for (int i = 1; i <= 5; i++) {
			League league = new League();
			league.setName("League "+i);
			getScoreBoardService().saveLeague(league);
			leagues.add(league);
		}
		
		/*
		 * Players
		 */
		List<Player> players = new ArrayList<Player>();
		for (int i = 1; i <= 10; i++) {
			Player player = new Player();
			player.setName("Player "+i);
			player.setFullName("Player Full Name "+i);
			getScoreBoardService().savePlayer(player);
			players.add(player);

			User user = new User();
			user.setUsername("username"+i);
			user.setDefaultLeague(getRandomLeague(leagues));
			getScoreBoardService().createUser(user, "password"+i);
		}
		
		/*
		 * Games
		 */
		DateMidnight date = new DateMidnight().minusMonths(1);
		DateMidnight today = new DateMidnight();
		while (date.isBefore(today)) {
			for (League league : leagues) {
				Game game = new Game();
				game.setDate(date.toDate());

				GameTeam gameTeam1 = new GameTeam();
				gameTeam1.setScore(getRandomScore(-1));
				gameTeam1.setGame(game);
				Team team1 = new Team();
				team1.setName("Team 1 "+date);
				Player player1 = getRandomPlayer(players, (Player[]) null);
				team1.addPlayer(player1);
				Player player2 = getRandomPlayer(players, player1);
				team1.addPlayer(player2);
				gameTeam1.setTeam(team1);
				game.setTeam1(gameTeam1);

				GameTeam gameTeam2 = new GameTeam();
				gameTeam2.setScore(getRandomScore(gameTeam1.getScore()));
				gameTeam2.setGame(game);
				Team team2 = new Team();
				team2.setName("Team 2 "+date);
				Player player3 = getRandomPlayer(players, player1, player2);
				team2.addPlayer(player3);
				Player player4 = getRandomPlayer(players, player1, player2, player3);
				team2.addPlayer(player4);
				gameTeam2.setTeam(team2);
				game.setTeam2(gameTeam2);
				game.setLeague(league);

				getScoreBoardService().saveGame(game);
			}

			date = date.plusDays(1);
		}
	}

	private int getRandomScore(int disallowedScore) {
		int score = -1;
		do {
			score = RandomUtils.nextInt(10)+1;
		} while(score == disallowedScore);
		return score;
	}

	private Player getRandomPlayer(List<Player> players, Player... disallowedPlayers) {
		Player player = null;
		Set<Player> disallowedPlayerSet = new HashSet<Player>();
		if (disallowedPlayers != null) {
			player = disallowedPlayers[0];
			disallowedPlayerSet.addAll(Arrays.asList(disallowedPlayers));
		}
		do {
			player = players.get(RandomUtils.nextInt(players.size()));
		} while(disallowedPlayerSet.contains(player));
		return player;
	}

	private League getRandomLeague(List<League> leagues) {
		return leagues.get(RandomUtils.nextInt(leagues.size()));
	}

	@After
	public void clearData() {
		TestMapper testMapper = applicationContext.getBean("testMapper", TestMapper.class);
		testMapper.clearGames();
		testMapper.clearGameTeams();
		testMapper.clearTeamPlayers();
		testMapper.clearTeams();
		testMapper.clearPlayers();
		testMapper.clearUsers();
	}

	protected static ScoreBoardService getScoreBoardService() {
		return applicationContext.getBean("scoreBoardService", ScoreBoardService.class);
	}

}
