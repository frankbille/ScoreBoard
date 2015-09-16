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

import dk.frankbille.scoreboard.domain.Game;

public class GameComparator implements Comparator<Game> {
	@Override
	public int compare(Game o1, Game o2) {
		int compare = o2.getDate().compareTo(o1.getDate());

		if (compare == 0) {
			compare = o2.getId().compareTo(o1.getId());
		}

		return compare;
	}
}