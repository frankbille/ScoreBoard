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

import org.apache.wicket.Page;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

class MenuItemPageLink implements Serializable, MenuItem {
	private static final long serialVersionUID = 1L;

	private MenuItemType menuItemType;
	private IModel<String> label;
	private Class<? extends Page> pageClass;
	private PageParameters pageParameters;
	private String icon;

	public MenuItemPageLink(MenuItemType menuItemType, IModel<String> label, Class<? extends Page> pageClass) {
		this(menuItemType, label, pageClass, null, null);
	}
	
	public MenuItemPageLink(MenuItemType menuItemType, IModel<String> label, Class<? extends Page> pageClass, String icon) {
		this(menuItemType, label, pageClass, null, icon);
	}
	
	public MenuItemPageLink(MenuItemType menuItemType, IModel<String> label, Class<? extends Page> pageClass, PageParameters pageParameters) {
		this(menuItemType, label, pageClass, pageParameters, null);
	}
	
	public MenuItemPageLink(MenuItemType menuItemType, IModel<String> label, Class<? extends Page> pageClass, PageParameters pageParameters, String icon) {
		this.menuItemType = menuItemType;
		this.label = label;
		this.pageClass = pageClass;
		this.pageParameters = pageParameters;
		this.icon = icon;
	}

	@Override
	public IModel<String> getLabel() {
		return label;
	}

	@Override
	public MenuItemType getMenuItemType() {
		return menuItemType;
	}
	
	public Class<? extends Page> getPageClass() {
		return pageClass;
	}
	
	public PageParameters getPageParameters() {
		return pageParameters;
	}
	
	public String getIconName() {
		return icon;
	}
	
	public void setIcon(String icon) {
		this.icon = icon;
	}
	
}