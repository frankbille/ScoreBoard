package dk.frankbille.scoreboard.daily;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
		tester.startPage(DailyGamePage.class);
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
		addGameTeam(game, 10, "Team 1", player1, player2);
		scoreBoardService.saveGame(game);
		addGameTeam(game, 7, "Team 2", player3, player4);
		scoreBoardService.saveGame(game);

		tester.startPage(DailyGamePage.class);
		tester.assertRenderedPage(DailyGamePage.class);
	}

	private void addGameTeam(Game game, int score, String teamName, Player... players) {
		GameTeam gameTeam1 = new GameTeam();
		gameTeam1.setScore(score);
		gameTeam1.setGame(game);
		Team team1 = new Team();
		team1.setName(teamName);
		Set<Player> team1Players = new HashSet<Player>();
		for (Player player : players) {
			team1Players.add(player);
		}
		team1.setPlayers(team1Players);
		gameTeam1.setTeam(team1);
		game.addTeam(gameTeam1);
	}

}
