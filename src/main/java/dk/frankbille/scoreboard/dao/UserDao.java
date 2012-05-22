package dk.frankbille.scoreboard.dao;

import dk.frankbille.scoreboard.domain.User;

public interface UserDao {

	User authenticate(String username, String password);

	void createUser(User user);

	boolean hasUserWithUsername(String username);

}
