/*
 * ScoreBoard
 * Copyright (C) 2012-2013 Frank Bille
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
