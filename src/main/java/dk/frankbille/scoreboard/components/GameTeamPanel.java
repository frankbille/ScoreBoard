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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.wicket.Application;
import org.apache.wicket.Localizer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import dk.frankbille.scoreboard.comparators.PlayerComparator;
import dk.frankbille.scoreboard.domain.GameTeam;
import dk.frankbille.scoreboard.domain.Player;
import dk.frankbille.scoreboard.domain.Team;
import dk.frankbille.scoreboard.player.PlayerPage;
import dk.frankbille.scoreboard.ratings.GamePlayerRatingInterface;
import dk.frankbille.scoreboard.ratings.GameRatingInterface;
import dk.frankbille.scoreboard.ratings.RatingCalculator;

public class GameTeamPanel extends Panel {
	private static final long serialVersionUID = 1L;

	private static final DecimalFormat RATING_VALUE = new DecimalFormat("#,##0");
	private static final DecimalFormat RATING_CHANGE = new DecimalFormat("+0.0;-0.0");

	public GameTeamPanel(String id, final IModel<GameTeam> model, final IModel<Player> selectedPlayerModel, final RatingCalculator rating) {
		super(id, model);

		final IModel<List<Player>> playersModel = new LoadableDetachableModel<List<Player>>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<Player> load() {
				GameTeam gameTeam = model.getObject();
				Team team = gameTeam.getTeam();
				List<Player> players = new ArrayList<Player>(team.getPlayers());
				Collections.sort(players, new PlayerComparator());
				return players;
			}
		};

        WebMarkupContainer gameTeamPanel = new WebMarkupContainer("gameTeamPanel");
        add(gameTeamPanel);

		// Popover - Only show if RatingCalculator is available
        if (rating != null) {
            gameTeamPanel.add(new PopoverBehavior(new StringResourceModel("rating", null), new AbstractReadOnlyModel<CharSequence>() {
                private static final long serialVersionUID = 1L;

                @Override
                public CharSequence getObject() {
                    StringBuilder b = new StringBuilder();

                    b.append("<small>");

                    GameTeam gameTeam = model.getObject();
                    List<Player> players = playersModel.getObject();

                    Localizer localizer = Application.get().getResourceSettings().getLocalizer();

                    // Player ratings
                    for (Player player : players) {
                        b.append(player.getName()).append(": ");

                        GamePlayerRatingInterface playerRating = rating.getGamePlayerRating(gameTeam.getGame().getId(), player.getId());
                        b.append(RATING_VALUE.format(playerRating.getRating()));

                        b.append(" <sup>");
                        b.append(RATING_CHANGE.format(playerRating.getChange()));
                        b.append("</sup><br>");
                    }

                    // Team rating
                    GameRatingInterface gameRatingChange = rating.getGameRatingChange(gameTeam.getGame().getId());
                    b.append("<strong>");
                    b.append(localizer.getString("team", GameTeamPanel.this)).append(": ");
                    b.append(RATING_VALUE.format(gameRatingChange.getRating(gameTeam.getId())));
                    b.append(" ");
                    double change = gameRatingChange.getChange(gameTeam.getId());

                    int playerCount = players.size();
                    if (playerCount > 0) {
                        change /= playerCount;
                    }
                    b.append(" <sup>");
                    b.append(RATING_CHANGE.format(change));

                    b.append("</sup></strong></small>");

                    return b;
                }
            }));
        }

		// Players
		gameTeamPanel.add(new ListView<Player>("players", playersModel) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final ListItem<Player> item) {
                final Player player = item.getModelObject();

                PageParameters pp = new PageParameters();
                pp.set(0, player.getId());
                BookmarkablePageLink<Void> playerLink = new BookmarkablePageLink<Void>("playerLink", PlayerPage.class, pp);
                item.add(playerLink);
                playerLink.add(new Label("name", new PropertyModel<String>(item.getModel(), "name")));

                item.add(new Label("plus", "+") {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public boolean isVisible() {
                        return item.getIndex() < getViewSize() - 1;
                    }
                });
            }
        });
	}

}
