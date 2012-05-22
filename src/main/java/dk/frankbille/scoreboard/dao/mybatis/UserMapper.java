package dk.frankbille.scoreboard.dao.mybatis;

import org.apache.ibatis.annotations.Param;

import dk.frankbille.scoreboard.domain.User;

public interface UserMapper {

	User authenticate(@Param("username") String username, @Param("password") String password);

	int insertUser(User user);

	User getUserWithUsername(String username);

}
