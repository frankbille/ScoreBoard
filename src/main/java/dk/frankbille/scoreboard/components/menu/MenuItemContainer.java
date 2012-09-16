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

}
