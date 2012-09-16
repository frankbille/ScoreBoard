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
