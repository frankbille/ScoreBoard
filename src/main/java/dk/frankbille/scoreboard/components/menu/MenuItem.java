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