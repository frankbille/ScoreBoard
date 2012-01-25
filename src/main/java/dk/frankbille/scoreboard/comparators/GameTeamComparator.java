package dk.frankbille.scoreboard.comparators;

import java.util.Comparator;

import dk.frankbille.scoreboard.domain.GameTeam;

public class GameTeamComparator implements Comparator<GameTeam> {
	@Override
	public int compare(GameTeam o1, GameTeam o2) {
		int compare = 0;

		compare = new Integer(o2.getScore()).compareTo(o1.getScore());

		return compare;
	}
}