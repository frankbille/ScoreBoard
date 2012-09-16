package dk.frankbille.scoreboard.domain;

import java.io.Serializable;

public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	private String username;
	private Player player;
	private League defaultLeague;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public League getDefaultLeague() {
		return defaultLeague;
	}
	
	public void setDefaultLeague(League defaultLeague) {
		this.defaultLeague = defaultLeague;
	}

}
