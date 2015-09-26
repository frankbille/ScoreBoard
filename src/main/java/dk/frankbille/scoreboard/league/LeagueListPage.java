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

package dk.frankbille.scoreboard.league;

import dk.frankbille.scoreboard.BasePage;
import dk.frankbille.scoreboard.components.menu.MenuItemType;
import dk.frankbille.scoreboard.domain.League;
import dk.frankbille.scoreboard.security.SecureExecutionBookmarkablePageLink;
import dk.frankbille.scoreboard.security.SecureRenderingBookmarkablePageLink;
import dk.frankbille.scoreboard.service.ScoreBoardService;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

public class LeagueListPage extends BasePage {
    private static final long serialVersionUID = 1L;
    @SpringBean
    private ScoreBoardService scoreBoardService;

    public LeagueListPage() {
        add(new SecureRenderingBookmarkablePageLink<League>("addNewLeagueLink", LeagueEditPage.class));

        IModel<List<League>> leagueListModel = new LoadableDetachableModel<List<League>>() {
            private static final long serialVersionUID = 1L;

            @Override
            protected List<League> load() {
                return scoreBoardService.getAllLeagues();
            }
        };

        add(new ListView<League>("leagues", leagueListModel) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem<League> item) {
                PageParameters pp = new PageParameters();
                pp.set(0, item.getModelObject().getId());
                Link<League> link = new SecureExecutionBookmarkablePageLink<League>("leagueLink", LeagueEditPage.class, pp);
                link.add(new Label("name", new PropertyModel<String>(item.getModel(), "name")));
                item.add(link);

                item.add(new Label("active", new StringResourceModel("active.${active}", item.getModel())));
                item.add(new Label("ratingCalculator",new PropertyModel<String>(item.getModel(), "ratingCalculator.name")));
            }
        });
    }

    @Override
    public MenuItemType getMenuItemType() {
        return MenuItemType.LEAGUES;
    }

}
