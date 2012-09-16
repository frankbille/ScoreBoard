package dk.frankbille.scoreboard.components.menu;

import org.apache.wicket.model.IModel;

interface MenuItem {

	IModel<String> getLabel();

	MenuItemType getMenuItemType();

}