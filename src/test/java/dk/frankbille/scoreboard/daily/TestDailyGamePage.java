package dk.frankbille.scoreboard.daily;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.junit.Test;

import dk.frankbille.scoreboard.domain.Game;
import dk.frankbille.scoreboard.domain.GameTeam;
import dk.frankbille.scoreboard.domain.Player;
import dk.frankbille.scoreboard.domain.Team;
import dk.frankbille.scoreboard.service.ScoreBoardService;
import dk.frankbille.scoreboard.test.WicketSpringTestCase;

public class TestDailyGamePage extends WicketSpringTestCase {

	@Test
	public void testBasicRendering() {
		PageParameters pp = new PageParameters();
		pp.add("league", 1);
		tester.startPage(DailyGamePage.class, pp);
		tester.assertRenderedPage(DailyGamePage.class);
	}

	@Test
	public void testRenderWithData() {
		ScoreBoardService scoreBoardService = getScoreBoardService();
		Player player1 = scoreBoardService.createNewPlayer("Player 1");
		Player player2 = scoreBoardService.createNewPlayer("Player 2");
		Player player3 = scoreBoardService.createNewPlayer("Player 3");
		Player player4 = scoreBoardService.createNewPlayer("Player 4");

		Game game = new Game();
		game.setDate(new Date());
		GameTeam gameTeam1 = createGameTeam(10, "Team 1", player1, player2);
		game.setTeam1(gameTeam1);
		gameTeam1.setGame(game);
		GameTeam gameTeam2 = createGameTeam(7, "Team 2", player3, player4);
		game.setTeam2(gameTeam2);
		gameTeam2.setGame(game);
		scoreBoardService.saveGame(game);

		tester.startPage(DailyGamePage.class);
		tester.assertRenderedPage(DailyGamePage.class);
	}

	private GameTeam createGameTeam(int score, String teamName, Player... players) {
		GameTeam gameTeam = new GameTeam();
		gameTeam.setScore(score);
		Team team = new Team();
		team.setName(teamName);
		Set<Player> teamPlayers = new HashSet<Player>();
		for (Player player : players) {
			teamPlayers.add(player);
		}
		team.setPlayers(teamPlayers);
		gameTeam.setTeam(team);
		return gameTeam;
	}

}
