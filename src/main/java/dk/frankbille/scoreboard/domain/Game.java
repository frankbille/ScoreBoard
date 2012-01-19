package dk.frankbille.scoreboard.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="game")
public class Game implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;

	private Date date;

	private List<GameTeam> teams;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Temporal(TemporalType.DATE)
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@OneToMany(mappedBy="game", fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	@OrderBy("id")
	public List<GameTeam> getTeams() {
		return teams;
	}

	public void addTeam(GameTeam gameTeam) {
		if (teams == null) {
			teams = new ArrayList<GameTeam>();
		}

		teams.add(gameTeam);
	}

	public void setTeams(List<GameTeam> teams) {
		this.teams = teams;
	}
}
