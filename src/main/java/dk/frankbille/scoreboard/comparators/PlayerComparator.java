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
