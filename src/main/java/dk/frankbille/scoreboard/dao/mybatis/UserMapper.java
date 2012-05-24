package dk.frankbille.scoreboard.dao.mybatis;

import org.apache.ibatis.annotations.Param;

import dk.frankbille.scoreboard.domain.Player;
import dk.frankbille.scoreboard.domain.User;

public interface UserMapper {

	User authenticate(@Param("username") String username, @Param("password") String password);

	int insertUser(@Param("user") User user, @Param("password") String password);

	int updateUser(User user);

	User getUserWithUsername(String username);

	User getUserForPlayer(Player player);

}
