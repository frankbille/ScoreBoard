package dk.frankbille.scoreboard.dao;

import dk.frankbille.scoreboard.domain.Player;
import dk.frankbille.scoreboard.domain.User;

public interface UserDao {

	User authenticate(String username, String password);

	void createUser(User user, String password);

	void updateUser(User user);

	boolean hasUserWithUsername(String username);

	User getUserForPlayer(Player player);

}
