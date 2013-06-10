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
		clearData();
		
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
