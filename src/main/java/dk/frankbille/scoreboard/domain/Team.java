package dk.frankbille.scoreboard.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name="team")
public class Team implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;

	private String name;

	private Set<Player> players;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
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

	@ManyToMany(cascade=CascadeType.ALL, targetEntity=Player.class, fetch=FetchType.EAGER)
	@JoinTable(name="team_players", joinColumns=@JoinColumn(name="team_id"), inverseJoinColumns=@JoinColumn(name="player_id"))
	public Set<Player> getPlayers() {
		if (players == null) {
			players = new HashSet<Player>();
		}

		return players;
	}

	public void setPlayers(Set<Player> players) {
		this.players = players;
	}

}
