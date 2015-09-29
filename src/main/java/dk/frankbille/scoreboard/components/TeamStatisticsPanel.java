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

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dk.frankbille.scoreboard.domain.*;
import org.apache.wicket.Application;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Localizer;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import dk.frankbille.scoreboard.ratings.RatingCalculator;
import dk.frankbille.scoreboard.service.ScoreBoardService;

public class TeamStatisticsPanel extends Panel {
	private static final long serialVersionUID = 1L;

	@SpringBean
	private ScoreBoardService scoreBoardService;

	public TeamStatisticsPanel(String id, final League league, final RatingCalculator rating) {
		super(id);

		IModel<List<TeamResult>> teamResultsModel = new LoadableDetachableModel<List<TeamResult>>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<TeamResult> load() {
				List<TeamResult> teamResults = scoreBoardService.getTeamResults(league);

				Collections.sort(teamResults, new Comparator<TeamResult>() {
					@Override
					public int compare(TeamResult o1, TeamResult o2) {
						double rating1 = rating.getTeamRating(o1.getTeam()).getRating();
						Double rating2 = rating.getTeamRating(o2.getTeam()).getRating();
						int compare = rating2.compareTo(rating1);

						if (compare == 0) {
							compare = new Double(o2.getGamesWonRatio()).compareTo(o1.getGamesWonRatio());
						}

						if (compare == 0) {
							compare = new Integer(o2.getGamesWon()).compareTo(o1.getGamesWon());
						}

						if (compare == 0) {
							compare = new Integer(o1.getGamesLost()).compareTo(o2.getGamesLost());
						}

						if (compare == 0) {
							compare = new Integer(o1.getGamesCount()).compareTo(o2.getGamesCount());
						}

						return compare;
					}
				});

				return teamResults;
			}
		};

		add(new ListView<TeamResult>("teams", teamResultsModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<TeamResult> item) {
				final TeamResult teamResult = item.getModelObject();

				item.add(new TooltipBehavior(new AbstractReadOnlyModel<CharSequence>() {
					private static final long serialVersionUID = 1L;

					@Override
					public CharSequence getObject() {
						StringBuilder b = new StringBuilder();
						Localizer localizer = Application.get().getResourceSettings().getLocalizer();
						b.append(localizer.getString("won", item)).append(": ").append(teamResult.getGamesWon());
						b.append(", ");
						b.append(localizer.getString("lost", item)).append(": ").append(teamResult.getGamesLost());
						return b;
					}
				}));

				item.add(RowColorModifier.create(item));
				item.add(new Label("number", ""+(item.getIndex()+1)));
				item.add(new Label("name", new PropertyModel<Integer>(item.getModel(), "name")));
				WebComponent medal = new WebComponent("medal") {
					private static final long serialVersionUID = 1L;

					@Override
					public boolean isVisible() {
						return item.getIndex() < 3;
					}
				};
				medal.add(AttributeModifier.append("class", new AbstractReadOnlyModel<String>() {
					private static final long serialVersionUID = 1L;

					@Override
					public String getObject() {
						if (item.getIndex() == 0) {
							return "gold";
						} else if (item.getIndex() == 1) {
							return "silver";
						} else if (item.getIndex() == 2) {
							return "bronze";
						}

						return null;
					}
				}));
				item.add(medal);
				item.add(new Label("gamesCount", new PropertyModel<Integer>(item.getModel(), "gamesCount")));
				item.add(new Label("winRatio", new FormatModel(new DecimalFormat("0.00"), new PropertyModel<Double>(item.getModel(), "gamesWonRatio"))));
				item.add(new Label("rating", new FormatModel(new DecimalFormat("#"), rating.getTeamRating(teamResult.getTeam()).getRating())));
				item.add(new Label("trend", new StringResourceModel(item.getModelObject().getTrend().name().toLowerCase(), null)));
			}
		});
	}

}
