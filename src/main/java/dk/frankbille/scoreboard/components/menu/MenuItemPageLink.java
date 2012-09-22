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