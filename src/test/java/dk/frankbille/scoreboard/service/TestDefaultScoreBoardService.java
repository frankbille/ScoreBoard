package dk.frankbille.scoreboard.service;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import dk.frankbille.scoreboard.domain.Game;
import dk.frankbille.scoreboard.test.WicketSpringTestCase;

public class TestDefaultScoreBoardService extends WicketSpringTestCase {

	@Test
	public void getAllGames() {		
		List<Game> allGames = getScoreBoardService().getAllGames();
		assertNotNull(allGames);
	}

}
