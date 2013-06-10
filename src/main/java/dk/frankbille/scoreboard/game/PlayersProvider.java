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

package dk.frankbille.scoreboard.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.vaynberg.wicket.select2.Response;
import com.vaynberg.wicket.select2.TextChoiceProvider;

import dk.frankbille.scoreboard.domain.Player;
import dk.frankbille.scoreboard.service.ScoreBoardService;

public class PlayersProvider extends TextChoiceProvider<Player> {
	private static final long serialVersionUID = 1L;

	@SpringBean
	private ScoreBoardService scoreBoardService;
	
	public PlayersProvider() {
		Injector.get().inject(this);
	}
	
	@Override
	protected String getDisplayText(Player player) {
		return player.getName();
	}

	@Override
	protected Object getId(Player player) {
		return player.getId();
	}

	@Override
	public void query(String term, int page, Response<Player> response) {
		List<Player> foundPlayers = scoreBoardService.searchPlayers(term);
		response.addAll(foundPlayers);
		response.setHasMore(false);
	}

	@Override
	public Collection<Player> toChoices(Collection<String> ids) {
		List<Player> players = new ArrayList<Player>();
		for (String idString : ids) {
			long playerId = Long.parseLong(idString);
			players.add(scoreBoardService.getPlayer(playerId));
		}
		return players;
	}

}
