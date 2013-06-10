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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.model.IModel;

public class MenuItemContainer implements MenuItem, Serializable {
	private static final long serialVersionUID = 1L;
	
	private MenuItemType menuItemType;
	private IModel<String> label;
	private List<MenuItem> subMenuItems = new ArrayList<MenuItem>();
	private String icon;

	public MenuItemContainer(MenuItemType menuItemType, IModel<String> label) {
		this.menuItemType = menuItemType;
		this.label = label;
	}

	@Override
	public IModel<String> getLabel() {
		return label;
	}

	@Override
	public MenuItemType getMenuItemType() {
		return menuItemType;
	}

	public void addSubMenuItem(MenuItem subMenuItem) {
		subMenuItems.add(subMenuItem);
	}

	public List<MenuItem> getSubMenuItems() {
		return subMenuItems;
	}
	
	public String getIconName() {
		return icon;
	}
	
	public void setIcon(String icon) {
		this.icon = icon;
	}

}
