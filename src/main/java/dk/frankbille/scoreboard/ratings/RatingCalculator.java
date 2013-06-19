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

package dk.frankbille.scoreboard.ratings;

import java.util.List;

import dk.frankbille.scoreboard.domain.Game;
import dk.frankbille.scoreboard.domain.PlayerResult;
import dk.frankbille.scoreboard.domain.TeamId;
import dk.frankbille.scoreboard.domain.TeamResult;

public interface RatingCalculator {
	public RatingInterface getPlayerRating(long playerId);
	public GamePlayerRatingInterface getGamePlayerRating(long gameId, long playerId);
	public void setGames(List<Game> games);
	public GameRatingInterface getGameRatingChange(Long id);
	public RatingInterface getTeamRating(TeamId team);
}
