package dk.frankbille.scoreboard.domain;

import java.io.Serializable;

public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	private String username;
	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public User copy() {
		User user = new User();
		user.setUsername(getUsername());
		user.setPassword(getPassword());
		return user;
	}

}
