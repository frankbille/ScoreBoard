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

package dk.frankbille.scoreboard.components.menu;

import org.apache.wicket.model.IModel;

interface MenuItem {

	IModel<String> getLabel();

	MenuItemType getMenuItemType();

	/**
	 * @return The icon name, as taken from
	 *         http://fortawesome.github.com/Font-Awesome/#all-icons WITHOUT
	 *         icon- in the beginning
	 */
	String getIconName();

}