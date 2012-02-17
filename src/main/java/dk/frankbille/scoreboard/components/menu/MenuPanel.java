package dk.frankbille.scoreboard.components.menu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;

import dk.frankbille.scoreboard.components.menu.MenuPanel.MenuItemType;
import dk.frankbille.scoreboard.daily.DailyGamePage;
import dk.frankbille.scoreboard.player.PlayerListPage;

public class MenuPanel extends GenericPanel<MenuItemType> {
	private static final long serialVersionUID = 1L;

	public static enum MenuItemType {
		DAILY,
		PLAYERS
	}

	static abstract class MenuItem implements Serializable {
		private static final long serialVersionUID = 1L;

		private MenuItemType menuItemType;
		private String label;

		public MenuItem(MenuItemType menuItemType, String label) {
			this.menuItemType = menuItemType;
			this.label = label;
		}

		public String getLabel() {
			return label;
		}

		public MenuItemType getMenuItemType() {
			return menuItemType;
		}

		protected abstract void onClick(AjaxRequestTarget target);
	}

	public MenuPanel(String id, IModel<MenuItemType> activeMenuItemModel) {
		super(id, activeMenuItemModel);

		IModel<List<MenuItem>> itemsModel = new LoadableDetachableModel<List<MenuItem>>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<MenuItem> load() {
				List<MenuItem> items = new ArrayList<MenuItem>();

				items.add(new MenuItem(MenuItemType.DAILY, "Daily") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onClick(AjaxRequestTarget target) {
						getRequestCycle().setResponsePage(DailyGamePage.class);
					}
				});

				items.add(new MenuItem(MenuItemType.PLAYERS, "Players") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onClick(AjaxRequestTarget target) {
						getRequestCycle().setResponsePage(PlayerListPage.class);
					}
				});

				return items;
			}
		};

		add(new ListView<MenuItem>("menuItems", itemsModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<MenuItem> listItem) {
				listItem.add(new AttributeAppender("class", new AbstractReadOnlyModel<String>() {
					private static final long serialVersionUID = 1L;

					@Override
					public String getObject() {
						MenuItemType activeMenuType = MenuPanel.this.getModelObject();
						MenuItemType menuItemType = listItem.getModelObject().getMenuItemType();

						return activeMenuType == menuItemType ? "active" : null;
					}
				}));

				AjaxLink<MenuItem> link = new AjaxLink<MenuPanel.MenuItem>("menuLink", listItem.getModel()) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						getModelObject().onClick(target);
					}
				};
				listItem.add(link);

				link.add(new Label("menuLabel", new PropertyModel<String>(listItem.getModel(), "label")));
			}
		});
	}

}
