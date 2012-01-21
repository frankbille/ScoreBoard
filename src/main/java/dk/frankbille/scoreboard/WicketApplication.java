package dk.frankbille.scoreboard;

import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;

import dk.frankbille.scoreboard.daily.DailyGamePage;

/**
 * Application object for your web application. If you want to run this
 * application without deploying, run the Start class.
 *
 * @see dk.frankbille.scoreboard.Start#main(String[])
 */
public class WicketApplication extends WebApplication {
	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<DailyGamePage> getHomePage() {
		return DailyGamePage.class;
	}

	/**
	 * @see org.apache.wicket.Application#init()
	 */
	@Override
	public void init() {
		super.init();

		getComponentInstantiationListeners().add(
				new SpringComponentInjector(this));

		mountPage("/daily", DailyGamePage.class);
	}
}
