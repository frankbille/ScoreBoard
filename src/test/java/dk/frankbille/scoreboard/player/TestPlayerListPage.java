package dk.frankbille.scoreboard.player;

import java.util.Arrays;

import org.junit.Test;

import dk.frankbille.scoreboard.domain.Player;
import dk.frankbille.scoreboard.service.ScoreBoardService;
import dk.frankbille.scoreboard.test.WicketSpringTestCase;

public class TestPlayerListPage extends WicketSpringTestCase {

	@Test
	public void testBasicRendering() {
		tester.startPage(PlayerListPage.class);
		tester.assertRenderedPage(PlayerListPage.class);
	}

	@Test
	public void testRenderWithData() {
		ScoreBoardService scoreBoardService = getScoreBoardService();
		Player player1 = scoreBoardService.createNewPlayer("Player 1");
		Player player2 = scoreBoardService.createNewPlayer("Player 2");
		Player player3 = scoreBoardService.createNewPlayer("Player 3");
		Player player4 = scoreBoardService.createNewPlayer("Player 4");

		tester.startPage(PlayerListPage.class);
		tester.assertRenderedPage(PlayerListPage.class);

		tester.assertListView("players", Arrays.asList(player1, player2, player3, player4));

		tester.assertLabel("players:0:playerLink:name", player1.getName());
		tester.assertLabel("players:1:playerLink:name", player2.getName());
		tester.assertLabel("players:2:playerLink:name", player3.getName());
		tester.assertLabel("players:3:playerLink:name", player4.getName());
	}

}
