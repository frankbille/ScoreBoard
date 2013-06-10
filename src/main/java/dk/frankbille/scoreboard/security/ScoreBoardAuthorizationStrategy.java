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

package dk.frankbille.scoreboard.security;

import org.apache.wicket.Component;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authorization.strategies.page.SimplePageAuthorizationStrategy;

import dk.frankbille.scoreboard.BasePage;
import dk.frankbille.scoreboard.ScoreBoardSession;

public class ScoreBoardAuthorizationStrategy extends SimplePageAuthorizationStrategy {

	public ScoreBoardAuthorizationStrategy() {
		super(SecureBasePage.class, BasePage.class);
	}

	@Override
	public boolean isActionAuthorized(Component component, Action action) {
		if (component instanceof RequiresLoginToRender && Component.RENDER.equals(action)) {
			return isAuthorized();
		} else if (component instanceof RequiresLoginToBeEnabled && Component.ENABLE.equals(action)) {
			return isAuthorized();
		}
		return true;
	}

	@Override
	protected boolean isAuthorized() {
		return ScoreBoardSession.get().isAuthenticated();
	}

}
