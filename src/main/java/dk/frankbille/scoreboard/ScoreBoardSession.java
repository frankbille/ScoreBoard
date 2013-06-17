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

	public void refreshUser(User user) {
		if (this.user == null) {
			throw new IllegalStateException("Not possible to refresh user if not authenticated");
		}

		if (this.user.getUsername().equals(user.getUsername()) == false) {
			throw new IllegalArgumentException("Not possible to refresh user if not the same");
		}

		this.user = user;
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
