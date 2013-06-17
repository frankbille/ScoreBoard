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

import dk.frankbille.scoreboard.ScoreBoardSession;
import dk.frankbille.scoreboard.daily.DailyGamePage;
import dk.frankbille.scoreboard.domain.League;
import dk.frankbille.scoreboard.domain.Player;
import dk.frankbille.scoreboard.game.EditGamePage;
import dk.frankbille.scoreboard.league.LeagueListPage;
import dk.frankbille.scoreboard.player.PlayerListPage;
import dk.frankbille.scoreboard.player.PlayerPage;
import dk.frankbille.scoreboard.security.LoginPage;
import dk.frankbille.scoreboard.security.LogoutPage;
import dk.frankbille.scoreboard.service.ScoreBoardService;
import org.apache.wicket.Application;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.resource.JQueryResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

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

                if (ScoreBoardSession.get().isAuthenticated()) {
                    items.add(new MenuItemPageLink(MenuItemType.GAME, new StringResourceModel("addGame", this), EditGamePage.class, "plus"));
                }

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
        itemsModel = new
                LoadableDetachableModel<List<MenuItem>>() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected List<MenuItem> load() {
                        List<MenuItem> items = new ArrayList<MenuItem>();

                        if (ScoreBoardSession.get().isAuthenticated()) {
                            final Player player = ScoreBoardSession.get().getUser().getPlayer();
                            if (player != null) {
                                PageParameters pp = new PageParameters();
                                pp.set(0, player.getId());
                                items.add(new MenuItemPageLink(MenuItemType.SECURE, new Model<String>(player.getName()), PlayerPage.class, pp, "user"));
                            }
                            items.add(new MenuItemPageLink(MenuItemType.LOGOUT, new StringResourceModel("logout", this), LogoutPage.class, "signout"));
                        } else {
                            items.add(new MenuItemPageLink(MenuItemType.SECURE, new StringResourceModel("loginOrCreate", this), LoginPage.class));
                        }


                        return items;
                    }
                };

        add(new MenuItemsPanel("rightMenuItems", itemsModel, activeMenuItemModel).setRenderBodyOnly(true));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(JavaScriptReferenceHeaderItem.forReference(JQueryResourceReference.get()));
    }
}
