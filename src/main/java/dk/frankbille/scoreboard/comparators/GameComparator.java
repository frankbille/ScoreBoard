package dk.frankbille.scoreboard.comparators;

import java.util.Comparator;

import dk.frankbille.scoreboard.domain.Game;

public class GameComparator implements Comparator<Game> {
	@Override
	public int compare(Game o1, Game o2) {
		int compare = 0;

		compare = o2.getDate().compareTo(o1.getDate());

		if (compare == 0) {
			compare = o2.getId().compareTo(o1.getId());
		}

		return compare;
	}
}