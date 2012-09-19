package dk.frankbille.scoreboard.components.menu;

import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;



public class MenuItemsPanel extends Panel {
	private static final long serialVersionUID = 1L;

	public MenuItemsPanel(String id, IModel<List<MenuItem>> itemsModel, final IModel<MenuItemType> activeMenuItemModel) {
		super(id, itemsModel);
		
		add(new ListView<MenuItem>("menuItems", itemsModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<MenuItem> listItem) {
				listItem.add(new AttributeAppender("class", new AbstractReadOnlyModel<String>() {
					private static final long serialVersionUID = 1L;

					@Override
					public String getObject() {
						MenuItemType activeMenuType = activeMenuItemModel.getObject();
						MenuItemType menuItemType = listItem.getModelObject().getMenuItemType();

						return activeMenuType == menuItemType ? "active" : null;
					}
				}));

				final WebMarkupContainer link;
				
				final MenuItem menuItem = listItem.getModelObject();
				if (menuItem instanceof MenuItemPageLink) {
					final MenuItemPageLink menuItemLink = (MenuItemPageLink) menuItem;

					link = new BookmarkablePageLink<Void>("menuLink", menuItemLink.getPageClass(), menuItemLink.getPageParameters());
					listItem.add(link);

					link.add(new Label("menuLabel", menuItemLink.getLabel()).setRenderBodyOnly(true));
					link.add(new WebMarkupContainer("downIcon").setVisible(false));
					
					listItem.add(new WebComponent("subMenu").setVisible(false));
				} else if (menuItem instanceof MenuItemContainer) {
					MenuItemContainer menuItemContainer = (MenuItemContainer) menuItem;
					
					listItem.add(AttributeModifier.append("class", "dropdown"));
					
					link = new WebMarkupContainer("menuLink");
					link.setOutputMarkupId(true);
					link.add(AttributeModifier.replace("href", "#"));
					link.add(AttributeModifier.replace("class", "dropdown-toggle"));
					link.add(AttributeModifier.replace("data-toggle", "dropdown"));
					link.add(AttributeModifier.replace("role", "button"));
					listItem.add(link);
					
					link.add(new Label("menuLabel", menuItemContainer.getLabel()).setRenderBodyOnly(true));
					link.add(new WebMarkupContainer("downIcon").setRenderBodyOnly(true));
					
					MenuItemsPanel subMenu = new MenuItemsPanel("subMenu", new PropertyModel<List<MenuItem>>(menuItemContainer, "subMenuItems"), new Model<MenuItemType>());
					subMenu.add(AttributeModifier.replace("class", "dropdown-menu"));
					subMenu.add(AttributeModifier.replace("role", "menu"));
					subMenu.add(AttributeModifier.replace("aria-labelledby", new AbstractReadOnlyModel<String>() {
						private static final long serialVersionUID = 1L;

						@Override
						public String getObject() {
							return link.getMarkupId();
						}
					}));
					listItem.add(subMenu);
				} else {
					throw new IllegalStateException("Unknown menuItem type: "+menuItem);
				}
				
				// Icon
				WebComponent icon = new WebComponent("icon") {
					private static final long serialVersionUID = 1L;
					
					@Override
					public boolean isVisible() {
						return menuItem.getIconName() != null;
					}
				};
				icon.add(AttributeModifier.replace("class", new AbstractReadOnlyModel<String>() {
					private static final long serialVersionUID = 1L;

					@Override
					public String getObject() {
						return "icon-"+menuItem.getIconName();
					}
				}));
				link.add(icon);
			}
		});
	}

	@Override
	public void renderHead(IHeaderResponse response) {
//		response.renderOnDomReadyJavaScript("$('.dropdown-toggle').dropdown()");
	}
	
}
