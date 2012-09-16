package dk.frankbille.scoreboard.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Game implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;

	private Date date;

	private GameTeam team1;
	
	private GameTeam team2;
	
	private League league;

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
	
	public GameTeam getTeam1() {
		return team1;
	}
	
	public void setTeam1(GameTeam team1) {
		this.team1 = team1;
	}
	
	public GameTeam getTeam2() {
		return team2;
	}
	
	public void setTeam2(GameTeam team2) {
		this.team2 = team2;
	}
	
	public League getLeague() {
		return league;
	}
	
	public void setLeague(League league) {
		this.league = league;
	}

	public boolean didTeamWin(GameTeam team) {
		if (team.getId() != team1.getId() && team.getId() != team2.getId()) {
			throw new IllegalArgumentException("The team was not one of the 2 teams in the game: "+team.getId());
		}
		
		return team.getScore() == getLargestScore();
	}
	
	public int getLargestScore() {
		return Math.max(team1.getScore(), team2.getScore());
	}

	public boolean hasPlayer(Player player) {
		return getTeamForPlayer(player) != null;
	}

	public GameTeam getTeamForPlayer(Player player) {
		if (team1.hasPlayer(player)) {
			return team1;
		} else if (team2.hasPlayer(player)) {
			return team2;
		} 
		return null;
	}

	public boolean didPlayerWin(Player player) {
		return didTeamWin(getTeamForPlayer(player));
	}
	
	public List<GameTeam> getTeamsSortedByScore() {
		List<GameTeam> teams = new ArrayList<GameTeam>();
		GameTeam firstTeam = getWinnerTeam();
		GameTeam secondTeam = getLoserTeam();
		if (firstTeam == null) {
			firstTeam = team1;
			secondTeam = team2;
		}
		teams.add(firstTeam);
		teams.add(secondTeam);
		return teams;
	}

	public GameTeam getWinnerTeam() {
		if (team1.getScore() == team2.getScore()) {
			return null;
		}
		
		return team1.getScore() > team2.getScore() ? team1 : team2;
	}

	public GameTeam getLoserTeam() {
		if (team1.getScore() == team2.getScore()) {
			return null;
		}
		
		return team1.getScore() < team2.getScore() ? team1 : team2;
	}
}
