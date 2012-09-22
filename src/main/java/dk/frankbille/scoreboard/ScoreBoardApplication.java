package dk.frankbille.scoreboard;

import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.mapper.MountedMapper;
import org.apache.wicket.request.mapper.parameter.UrlPathPageParametersEncoder;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;

import com.vaynberg.wicket.select2.ApplicationSettings;

import dk.frankbille.scoreboard.daily.DailyGamePage;
import dk.frankbille.scoreboard.game.EditGamePage;
import dk.frankbille.scoreboard.league.LeagueEditPage;
import dk.frankbille.scoreboard.league.LeagueListPage;
import dk.frankbille.scoreboard.player.PlayerEditPage;
import dk.frankbille.scoreboard.player.PlayerListPage;
import dk.frankbille.scoreboard.player.PlayerPage;
import dk.frankbille.scoreboard.security.LoginPage;
import dk.frankbille.scoreboard.security.LogoutPage;
import dk.frankbille.scoreboard.security.ScoreBoardAuthorizationStrategy;

/**
 * Application object for your web application. If you want to run this
 * application without deploying, run the Start class.
 *
 * @see dk.frankbille.scoreboard.Start#main(String[])
 */
public class ScoreBoardApplication extends WebApplication {
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
		
		// Always strip Wicket tags
		getMarkupSettings().setStripWicketTags(true);

		ApplicationSettings.get().setIncludeJquery(false);
		
		getComponentInstantiationListeners().add(
				new SpringComponentInjector(this));

		getSecuritySettings().setAuthorizationStrategy(new ScoreBoardAuthorizationStrategy());
		
		getMarkupSettings().setDefaultBeforeDisabledLink("");
		getMarkupSettings().setDefaultAfterDisabledLink("");

		mount(new MountedMapper("/daily", DailyGamePage.class, new UrlPathPageParametersEncoder()));
		mountPage("/game", EditGamePage.class);
		mountPage("/player/edit", PlayerEditPage.class);
		mountPage("/player", PlayerPage.class);
		mountPage("/players", PlayerListPage.class);
		mountPage("/leagues", LeagueListPage.class);
		mountPage("/league/edit", LeagueEditPage.class);
		mountPage("/login", LoginPage.class);
		mountPage("/logout", LogoutPage.class);
	}

	@Override
	public Session newSession(Request request, Response response) {
		return new ScoreBoardSession(request);
	}

}
