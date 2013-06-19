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

package dk.frankbille.scoreboard.service;

import java.util.List;

import dk.frankbille.scoreboard.domain.Game;
import dk.frankbille.scoreboard.domain.League;
import dk.frankbille.scoreboard.domain.Player;
import dk.frankbille.scoreboard.domain.PlayerResult;
import dk.frankbille.scoreboard.domain.TeamResult;
import dk.frankbille.scoreboard.domain.User;

public interface ScoreBoardService {

	Player createNewPlayer(String name);

	void savePlayer(Player player);

	List<Player> getAllPlayers();

	void saveGame(Game game);

	Game getGame(Long gameId);

	List<Game> getAllGames();

	List<Game> getAllGames(League league);

	List<Game> getAllGames(League league, int numberOfGames);

	List<PlayerResult> getPlayerResults();

	List<PlayerResult> getPlayerResults(League league);

	List<TeamResult> getTeamResults(League league);

	Player getPlayer(Long playerId);

	List<Game> getPlayerGames(Player player);

	boolean hasUserWithUsername(String username);

	void createUser(User user, String password);

	void updateUser(User user);

	User authenticate(String username, String password);

	User getUserForPlayer(Player object);

	List<Player> searchPlayers(String term);

	List<League> getAllLeagues();

	void saveLeague(League league);

	League getLeague(Long leagueId);
}
