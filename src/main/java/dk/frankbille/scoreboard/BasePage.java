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

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.PropertyModel;
import org.joda.time.DateMidnight;

import dk.frankbille.scoreboard.components.menu.MenuItemType;
import dk.frankbille.scoreboard.components.menu.MenuPanel;

public abstract class BasePage extends WebPage {
	private static final long serialVersionUID = 1L;

	public BasePage() {
		MenuPanel menuPanel = new MenuPanel("menu", new PropertyModel<MenuItemType>(this, "menuItemType"));
		menuPanel.setRenderBodyOnly(true);
		add(menuPanel);

		add(new Label("copyrightDate", new AbstractReadOnlyModel<CharSequence>() {
			private static final long serialVersionUID = 1L;

			@Override
			public CharSequence getObject() {
				int inceptionYear = 2012;
				int thisYear = new DateMidnight().getYear();

				StringBuilder sb = new StringBuilder();
				sb.append(inceptionYear);
				if (inceptionYear != thisYear) {
					sb.append("-");
					sb.append(thisYear);
				}
				return sb;
			}
		}));
	}

	public abstract MenuItemType getMenuItemType();

}
