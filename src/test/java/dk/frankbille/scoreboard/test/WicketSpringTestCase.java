package dk.frankbille.scoreboard.test;

import javax.servlet.ServletContext;

import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.mock.MockServletContext;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import dk.frankbille.scoreboard.ScoreBoardApplication;
import dk.frankbille.scoreboard.ScoreBoardSession;
import dk.frankbille.scoreboard.dao.mybatis.TestMapper;
import dk.frankbille.scoreboard.domain.User;
import dk.frankbille.scoreboard.service.ScoreBoardService;

public abstract class WicketSpringTestCase {

	private static ServletContext servletContext;
	protected WicketTester tester;
	protected static WebApplicationContext applicationContext;

	@BeforeClass
	public static void setupSpring() {
		if (applicationContext == null) {
			MockServletContext servletContext = new MockServletContext(new ScoreBoardApplication(), "src/main/webapp");
			servletContext.addInitParameter("contextConfigLocation", "classpath:applicationContext-test.xml");
			ContextLoader loader = new ContextLoader();
			applicationContext = loader.initWebApplicationContext(servletContext);
			WicketSpringTestCase.servletContext = servletContext;
		}
	}

	@Before
	public void setupWicket() {
		final User user = new User();
		user.setUsername("username1");
		getScoreBoardService().createUser(user, "password1");

		ScoreBoardApplication application = new ScoreBoardApplication() {
			@Override
			public Session newSession(Request request, Response response) {
				ScoreBoardSession session = (ScoreBoardSession) super.newSession(request, response);
				session.authenticate(user.getUsername(), "password1");
				return session;
			}
		};
		tester = new WicketTester(application, servletContext);
	}

	@After
	public void clearData() {
		TestMapper testMapper = applicationContext.getBean("testMapper", TestMapper.class);
		testMapper.clearGameTeams();
		testMapper.clearGames();
		testMapper.clearTeamPlayers();
		testMapper.clearTeams();
		testMapper.clearPlayers();
		testMapper.clearUsers();
	}

	protected ScoreBoardService getScoreBoardService() {
		return applicationContext.getBean("scoreBoardService", ScoreBoardService.class);
	}

}
