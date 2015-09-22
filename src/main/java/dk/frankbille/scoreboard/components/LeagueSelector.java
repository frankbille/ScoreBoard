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

package dk.frankbille.scoreboard.components;

import com.google.common.collect.Ordering;
import dk.frankbille.scoreboard.domain.League;
import dk.frankbille.scoreboard.service.ScoreBoardService;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.form.select.IOptionRenderer;
import org.apache.wicket.extensions.markup.html.form.select.Select;
import org.apache.wicket.extensions.markup.html.form.select.SelectOptions;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.*;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.Serializable;
import java.util.*;

public class LeagueSelector extends GenericPanel<League> {

    @SpringBean
    private ScoreBoardService scoreBoardService;

    public LeagueSelector(String id, IModel<League> model) {
        super(id, model);

        setRenderBodyOnly(true);

        final IModel<SortedMap<Boolean, Set<League>>> allLeaguesModel = new LoadableDetachableModel<SortedMap<Boolean, Set<League>>>() {
            @Override
            protected SortedMap<Boolean, Set<League>> load() {
                SortedMap<Boolean, Set<League>> groupedLeagues = new TreeMap<Boolean, Set<League>>(Ordering.natural().reverse());

                List<League> allLeagues = scoreBoardService.getAllLeagues();
                for (League league : allLeagues) {
                    Set<League> leaguesByState = groupedLeagues.get(league.isActive());
                    if (leaguesByState == null) {
                        leaguesByState = new HashSet<League>();
                        groupedLeagues.put(league.isActive(), leaguesByState);
                    }
                    leaguesByState.add(league);
                }

                return groupedLeagues;
            }
        };

        IModel<List<Boolean>> leagueStatesModel = new LoadableDetachableModel<List<Boolean>>() {
            @Override
            protected List<Boolean> load() {
                return new ArrayList<Boolean>(allLeaguesModel.getObject().keySet());
            }
        };

        Select<League> select = new Select<League>("select", model) {

        };
        select.add(new Select2Enabler());
        select.add(AttributeAppender.replace("class", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return LeagueSelector.this.getMarkupAttributes().getString("class", "");
            }
        }));
        add(select);

        select.add(new ListView<Boolean>("leagueGroups", leagueStatesModel) {

            @Override
            protected void populateItem(ListItem<Boolean> item) {
                item.add(AttributeAppender.replace("label", new StringResourceModel("active.${modelObject}", new Model<Serializable>(item))));

                List<League> leagueList = Ordering.usingToString().sortedCopy(allLeaguesModel.getObject().get(item.getModelObject()));

                item.add(new SelectOptions<League>("leagues", leagueList, new IOptionRenderer<League>() {
                    @Override
                    public String getDisplayValue(League league) {
                        return league.getName();
                    }

                    @Override
                    public IModel<League> getModel(League league) {
                        return new Model<League>(league);
                    }
                }));
            }
        });
    }
}
