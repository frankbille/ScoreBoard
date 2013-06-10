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

package dk.frankbille.scoreboard.player;

import org.apache.wicket.spring.injection.annot.SpringBean;

import dk.frankbille.scoreboard.components.InjectableDetachableModel;
import dk.frankbille.scoreboard.domain.Player;
import dk.frankbille.scoreboard.service.ScoreBoardService;

public class PlayerModel extends InjectableDetachableModel<Player> {
	private static final long serialVersionUID = 1L;

	private final Long playerId;

	@SpringBean
	private ScoreBoardService scoreBoardService;

	public PlayerModel(Long playerId) {
		this.playerId = playerId;
	}

	@Override
	protected Player load() {
		return scoreBoardService.getPlayer(playerId);
	}
}