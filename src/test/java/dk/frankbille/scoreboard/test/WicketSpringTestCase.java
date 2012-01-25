package dk.frankbille.scoreboard.test;

import javax.servlet.ServletContext;

import org.apache.wicket.protocol.http.mock.MockServletContext;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.web.context.ContextLoader;

import dk.frankbille.scoreboard.WicketApplication;

public abstract class WicketSpringTestCase {

	private static ServletContext servletContext;
	protected WicketTester tester;

	@BeforeClass
	public static void setupSpring() {
		MockServletContext servletContext = new MockServletContext(new WicketApplication(), "src/main/webapp");
		servletContext.addInitParameter("contextConfigLocation", "classpath:applicationContext-test.xml");
		ContextLoader loader = new ContextLoader();
		loader.initWebApplicationContext(servletContext);
		WicketSpringTestCase.servletContext = servletContext;
	}

	@Before
	public void setupWicket() {
		tester = new WicketTester(new WicketApplication(), servletContext);
	}

}
