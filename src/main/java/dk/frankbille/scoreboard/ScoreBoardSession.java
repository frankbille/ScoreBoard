package dk.frankbille.scoreboard;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.cookies.CookieUtils;
import org.apache.wicket.util.string.Strings;

import dk.frankbille.scoreboard.domain.User;
import dk.frankbille.scoreboard.service.ScoreBoardService;

public class ScoreBoardSession extends WebSession {
	private static final long serialVersionUID = 1L;

	private User user;

	@SpringBean
	private ScoreBoardService scoreBoardService;

	public ScoreBoardSession(Request request) {
		super(request);
		Injector.get().inject(this);

		CookieUtils cookieUtils = new CookieUtils();
		String loginUsername = cookieUtils.load("loginUsername");
		String loginPassword = cookieUtils.load("loginPassword");
		if (false == Strings.isEmpty(loginUsername) && false == Strings.isEmpty(loginPassword)) {
			authenticate(loginUsername, loginPassword);
		}
	}

	@Override
	public boolean authenticate(String username, String password) {
		user = scoreBoardService.authenticate(username, password);
		return isAuthenticated();
	}

	public boolean isAuthenticated() {
		return getUser() != null;
	}

	public User getUser() {
		return user;
	}

	public void logout() {
		CookieUtils cookieUtils = new CookieUtils();
		cookieUtils.remove("loginUsername");
		cookieUtils.remove("loginPassword");
		user = null;
		invalidate();
	}

	public static ScoreBoardSession get() {
		return (ScoreBoardSession) WebSession.get();
	}

}
