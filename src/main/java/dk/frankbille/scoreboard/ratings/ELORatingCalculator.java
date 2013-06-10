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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dk.frankbille.scoreboard.comparators.GameComparator;
import dk.frankbille.scoreboard.domain.Game;
import dk.frankbille.scoreboard.domain.GameTeam;
import dk.frankbille.scoreboard.domain.Player;

public class ELORatingCalculator implements RatingCalculator {
	private Map<Long,ELOPlayerRating> players;
	private Map<Long,ELOGameRating> games;
	private Map<String,ELOGamePlayerRating> gamePlayers;
	
	public ELORatingCalculator() {
		players = new HashMap<Long,ELOPlayerRating>();
		games = new HashMap<Long,ELOGameRating>();
		gamePlayers = new HashMap<String,ELOGamePlayerRating>();
	}
	
	@Override
	public void setGames(List<Game> games) {
		//Clear the current ratings
		players.clear();
		gamePlayers.clear();
		
		//Order the games by date
		Collections.sort(games, new GameComparator() {
			@Override
			public int compare(Game o1, Game o2) {
				int compare = 0;

				compare = -(o2.getDate().compareTo(o1.getDate()));

				if (compare == 0) {
					compare = -(o2.getId().compareTo(o1.getId()));
				}

				return compare;
			}
		});
		
		//Go through the games one-by-one
		for (Game game : games) {
			addTeamRatings(game);
		}
	}
	private void addTeamRatings(Game game) throws RatingException {
		//Calculate the teams before-ratings
		GameTeam winner = game.getWinnerTeam();
		int winnerScore = winner.getScore();
		double winnerRating = calculateTeamRating(winner);
		
		GameTeam loser = game.getLoserTeam();
		int loserScore = loser.getScore();
		double loserRating = calculateTeamRating(loser);
		
		//Check that we have 2 teams
		if (winner==null ||loser==null)
			throw new RatingException("ELORatingCalculator needs a winning and a loosing team");
		
		//Calculate the rating change for the team
		double change = ELOCalculator.calculate(winnerRating, winnerScore, loserRating, loserScore);
		
		//Add the rating change to the game and the players
		games.put(game.getId(), new ELOGameRating(winnerRating, loserRating, change));
		setRatingChange(winner,+change);
		setRatingChange(loser,-change);
	}

	private double calculateTeamRating(GameTeam team) {
		ELOTeamRatingBuilder ratingBuilder = new ELOTeamRatingBuilder(); 
		for (Player player : team.getTeam().getPlayers()) {
			ratingBuilder.addPlayer(getPlayerRating(player.getId()).getRating());
		}
		return ratingBuilder.getTeamRating();
	}
	
	@Override
	public ELOPlayerRating getPlayerRating(long playerId) {
		//Try to find the playerRating
		ELOPlayerRating player = players.get(playerId);
		
		if (player==null) {
			//Create a new default player
			player = new ELOPlayerRating();
			players.put(playerId, player);
		}
		
		return player;
	}

	private void setRatingChange(GameTeam team, double ratingChange) {
		//Find the rating change per player
		ratingChange = ratingChange/team.getTeam().getPlayers().size();
		
		for (Player player : team.getTeam().getPlayers()) {
			ELOPlayerRating playerRating = getPlayerRating(player.getId());
			addGamePlayerRating(
				team.getGame().getId(), player.getId(), 
				playerRating.getRating(), ratingChange);
			playerRating.changeRating(ratingChange);
		}
	}

	public void addGamePlayerRating(long gameId, long playerId, double rating, double change) {
		//Create a new gamePlayer
		gamePlayers.put(gameId+"-"+playerId, new ELOGamePlayerRating(rating, change));
	}

	@Override
	public GamePlayerRating getGamePlayerRating(long gameId, long playerId) {
		return gamePlayers.get(gameId+"-"+playerId);
	}

	@Override
	public GameRating getGameRatingChange(Long gameId) {
		return games.get(gameId);
	}
}
