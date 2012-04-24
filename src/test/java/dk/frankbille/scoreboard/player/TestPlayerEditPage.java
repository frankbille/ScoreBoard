package dk.frankbille.scoreboard.player;

import static org.junit.Assert.assertEquals;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Test;

import dk.frankbille.scoreboard.domain.Player;
import dk.frankbille.scoreboard.service.ScoreBoardService;
import dk.frankbille.scoreboard.test.WicketSpringTestCase;

public class TestPlayerEditPage extends WicketSpringTestCase {

	@Test
	public void testBasicRendering() {
		PageParameters pageParameters = new PageParameters();
		pageParameters.set(0, 2L);

		tester.startPage(PlayerEditPage.class, pageParameters);
		tester.assertRenderedPage(PlayerEditPage.class);
	}

	@Test
	public void testRenderWithData() {
		ScoreBoardService scoreBoardService = getScoreBoardService();
		scoreBoardService.createNewPlayer("Player 1");
		Player player2 = scoreBoardService.createNewPlayer("Player 2");
		player2.setFullName("Player 2 Full Name");
		player2.setGroupName("Player 2 Group");
		scoreBoardService.savePlayer(player2);
		scoreBoardService.createNewPlayer("Player 3");
		scoreBoardService.createNewPlayer("Player 4");

		PageParameters pageParameters = new PageParameters();
		pageParameters.set(0, player2.getId());

		tester.startPage(PlayerEditPage.class, pageParameters);
		tester.assertRenderedPage(PlayerEditPage.class);

		tester.assertLabel("name", player2.getName());
		FormTester formTester = tester.newFormTester("playerForm");
		assertEquals(player2.getName(), formTester.getTextComponentValue("nameField"));
		assertEquals(player2.getFullName(), formTester.getTextComponentValue("fullNameField"));
		assertEquals(player2.getGroupName(), formTester.getTextComponentValue("groupField"));
		formTester.setValue("nameField", "New Name");
		formTester.setValue("fullNameField", "New Full Name");
		formTester.setValue("groupField", "New Group");
		formTester.submit();

		tester.assertRenderedPage(PlayerListPage.class);

		Player updatedPlayer = scoreBoardService.getPlayer(player2.getId());
		assertEquals("New Name", updatedPlayer.getName());
		assertEquals("New Full Name", updatedPlayer.getFullName());
		assertEquals("New Group", updatedPlayer.getGroupName());
	}

}
