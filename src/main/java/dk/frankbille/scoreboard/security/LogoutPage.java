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

import org.apache.wicket.Application;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebComponent;

import dk.frankbille.scoreboard.BasePage;
import dk.frankbille.scoreboard.ScoreBoardSession;
import dk.frankbille.scoreboard.components.menu.MenuItemType;

public class LogoutPage extends BasePage {
	private static final long serialVersionUID = 1L;

	public LogoutPage() {
		ScoreBoardSession.get().logout();
		
		WebComponent refresh = new WebComponent("refresh"); //$NON-NLS-1$
		StringBuilder content = new StringBuilder();
		content.append("1; url="); //$NON-NLS-1$
		content.append(getRequestCycle().urlFor(Application.get().getHomePage(), null));
		refresh.add(AttributeModifier.replace("content", content)); //$NON-NLS-1$
		add(refresh);
	}

	@Override
	public MenuItemType getMenuItemType() {
		return MenuItemType.SECURE;
	}

}
