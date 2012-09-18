package dk.frankbille.scoreboard.components.menu;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Application;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import dk.frankbille.scoreboard.ScoreBoardSession;
import dk.frankbille.scoreboard.daily.DailyGamePage;
import dk.frankbille.scoreboard.domain.League;
import dk.frankbille.scoreboard.domain.Player;
import dk.frankbille.scoreboard.league.LeagueListPage;
import dk.frankbille.scoreboard.player.PlayerListPage;
import dk.frankbille.scoreboard.player.PlayerPage;
import dk.frankbille.scoreboard.security.LoginPage;
import dk.frankbille.scoreboard.security.LogoutPage;
import dk.frankbille.scoreboard.service.ScoreBoardService;

public class MenuPanel extends GenericPanel<MenuItemType> {
	private static final long serialVersionUID = 1L;
	
	@SpringBean
	private ScoreBoardService scoreBoardService;

	public MenuPanel(String id, final IModel<MenuItemType> activeMenuItemModel) {
		super(id);
		
		add(new BookmarkablePageLink<Void>("homeLink", Application.get().getHomePage()));

		/*
		 * STANDARD MENU
		 */
		IModel<List<MenuItem>> itemsModel = new LoadableDetachableModel<List<MenuItem>>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<MenuItem> load() {
				List<MenuItem> items = new ArrayList<MenuItem>();

				MenuItemContainer dailyMenu = new MenuItemContainer(MenuItemType.DAILY, new StringResourceModel("daily", this));
				List<League> leagues = scoreBoardService.getAllLeagues();
				for (League league : leagues) {
					PageParameters pp = new PageParameters();
					pp.add("league", league.getId());
					dailyMenu.addSubMenuItem(new MenuItemPageLink(MenuItemType.DAILY, new Model<String>(league.getName()), DailyGamePage.class, pp));
				}
				items.add(dailyMenu);

				items.add(new MenuItemPageLink(MenuItemType.PLAYERS, new StringResourceModel("players", this), PlayerListPage.class));

				items.add(new MenuItemPageLink(MenuItemType.LEAGUES, new StringResourceModel("leagues", this), LeagueListPage.class));

				return items;
			}
		};

		add(new MenuItemsPanel("menuItems", itemsModel, activeMenuItemModel).setRenderBodyOnly(true));
		
		/*
		 * RIGHT MENU
		 */
		itemsModel = new LoadableDetachableModel<List<MenuItem>>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<MenuItem> load() {
				List<MenuItem> items = new ArrayList<MenuItem>();

				if (ScoreBoardSession.get().isAuthenticated()) {
					final Player player = ScoreBoardSession.get().getUser().getPlayer();
					if (player != null) {
						PageParameters pp = new PageParameters();
						pp.set(0, player.getId());
						items.add(new MenuItemPageLink(MenuItemType.SECURE, new Model<String>(player.getName()), PlayerPage.class, pp));
					}
					items.add(new MenuItemPageLink(MenuItemType.LOGOUT, new StringResourceModel("logout", this), LogoutPage.class));
				} else {
					items.add(new MenuItemPageLink(MenuItemType.SECURE, new StringResourceModel("loginOrCreate", this), LoginPage.class));
				}


				return items;
			}
		};
		
		add(new MenuItemsPanel("rightMenuItems", itemsModel, activeMenuItemModel).setRenderBodyOnly(true));
	}

}
