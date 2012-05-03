package dk.frankbille.scoreboard.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Game implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;

	private Date date;

	private List<GameTeam> teams;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

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

	public boolean didTeamWin(GameTeam team) {
		int largestScore = -1;
		for (GameTeam gameTeam : teams) {
			if (gameTeam.getScore() > largestScore) {
				largestScore = gameTeam.getScore();
			}
		}

		return team.getScore() == largestScore;
	}
	
	public GameTeam getTeamForPlayer(Player player) {
		GameTeam foundGameTeam = null;
		for (GameTeam gameTeam : getTeams()) {
			if (gameTeam.hasPlayer(player)) {
				foundGameTeam = gameTeam;
				break;
			}
		}
		return foundGameTeam;
	}

	public boolean didPlayerWin(Player player) {
		return didTeamWin(getTeamForPlayer(player));
	}
}
