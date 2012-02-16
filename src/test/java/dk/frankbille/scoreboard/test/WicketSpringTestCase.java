package dk.frankbille.scoreboard.test;

import javax.servlet.ServletContext;

import org.apache.wicket.protocol.http.mock.MockServletContext;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import dk.frankbille.scoreboard.WicketApplication;
import dk.frankbille.scoreboard.dao.mybatis.TestMapper;
import dk.frankbille.scoreboard.service.ScoreBoardService;

public abstract class WicketSpringTestCase {

	private static ServletContext servletContext;
	protected WicketTester tester;
	protected static WebApplicationContext applicationContext;

	@BeforeClass
	public static void setupSpring() {
		if (applicationContext == null) {
			MockServletContext servletContext = new MockServletContext(new WicketApplication(), "src/main/webapp");
			servletContext.addInitParameter("contextConfigLocation", "classpath:applicationContext-test.xml");
			ContextLoader loader = new ContextLoader();
			applicationContext = loader.initWebApplicationContext(servletContext);
			WicketSpringTestCase.servletContext = servletContext;
		}
	}

	@Before
	public void setupWicket() {
		tester = new WicketTester(new WicketApplication(), servletContext);
	}

	@After
	public void clearData() {
		TestMapper testMapper = applicationContext.getBean("testMapper", TestMapper.class);
		testMapper.clearGameTeams();
		testMapper.clearGames();
		testMapper.clearTeamPlayers();
		testMapper.clearTeams();
		testMapper.clearPlayers();
	}

	protected ScoreBoardService getScoreBoardService() {
		return applicationContext.getBean("scoreBoardService", ScoreBoardService.class);
	}

}
