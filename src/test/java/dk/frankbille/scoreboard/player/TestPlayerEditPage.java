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

package dk.frankbille.scoreboard.player;

import static org.junit.Assert.assertEquals;

import org.apache.wicket.model.Model;
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

	@Test
	public void testRenderWithNewPlayer() {
		tester.startPage(new PlayerEditPage(new Model<Player>(new Player())));
		tester.assertRenderedPage(PlayerEditPage.class);
	}

}
