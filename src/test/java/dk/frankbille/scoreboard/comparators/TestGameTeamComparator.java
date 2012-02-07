package dk.frankbille.scoreboard.comparators;

import static junit.framework.Assert.assertTrue;

import org.junit.Test;

import dk.frankbille.scoreboard.domain.GameTeam;

public class TestGameTeamComparator {

	@Test
	public void testCompare() {
		GameTeamComparator c = new GameTeamComparator();

		GameTeam o1 = new GameTeam();
		o1.setScore(1);
		GameTeam o2 = new GameTeam();
		o2.setScore(2);
		assertTrue(c.compare(o1, o2) > 0);
	}

}
