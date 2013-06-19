package dk.frankbille.scoreboard.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;


public class TeamId {
	List<Long> players;

	public TeamId(Team team) {
		players = new ArrayList<Long>();
		for (Player player : team.getPlayers()) {
			players.add(player.getId());
		}
		Collections.sort(players);
	}

	public TeamId(GameTeam team) {
		this(team.getTeam());
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hashcode = new HashCodeBuilder();
		for (Long playerId : players) {
			hashcode.append(playerId);
		}
		return hashcode.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (false == obj instanceof TeamId) {
			return false;
		}

		TeamId other = (TeamId)obj;
		if (players.size() != other.players.size()) {
			return false;
		}

		EqualsBuilder equals = new EqualsBuilder();
		for (int i=0;i<players.size();i++) {
			equals.append(players.get(i), other.players.get(i));
		}
		return equals.isEquals();
	}

	@Override
	public String toString() {
		return "{RatingTeamId["+StringUtils.join(players,",")+"]}";
	}
}
