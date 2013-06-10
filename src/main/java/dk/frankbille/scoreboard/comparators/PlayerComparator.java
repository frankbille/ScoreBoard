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

package dk.frankbille.scoreboard.comparators;

import java.util.Comparator;

import dk.frankbille.scoreboard.domain.Player;

public class PlayerComparator implements Comparator<Player> {
	@Override
	public int compare(Player o1, Player o2) {
		int compare = 0;

		compare = o1.getName().compareTo(o2.getName());

		if (compare == 0) {
			compare = o1.getId().compareTo(o2.getId());
		}

		return compare;
	}
}
