package dk.frankbille.scoreboard.daily;

import org.junit.Test;

import dk.frankbille.scoreboard.test.WicketSpringTestCase;

public class TestDailyGamePage extends WicketSpringTestCase {

	@Test
	public void testBasicRendering() {
		tester.startPage(DailyGamePage.class);
		tester.assertRenderedPage(DailyGamePage.class);
	}

}
