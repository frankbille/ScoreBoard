package dk.frankbille.scoreboard.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Team implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;

	private String name;

	private Set<Player> players;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Player> getPlayers() {
		if (players == null) {
			players = new HashSet<Player>();
		}

		return players;
	}
	
	public void addPlayer(Player player) {
		getPlayers().add(player);
	}

	public void setPlayers(Set<Player> players) {
		this.players = players;
	}

}
