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

package dk.frankbille.scoreboard.ratings.elo;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dk.frankbille.scoreboard.comparators.GameComparator;
import dk.frankbille.scoreboard.domain.*;
import dk.frankbille.scoreboard.ratings.GamePlayerRatingInterface;
import dk.frankbille.scoreboard.ratings.GameRatingInterface;
import dk.frankbille.scoreboard.ratings.RatingCalculator;
import dk.frankbille.scoreboard.ratings.RatingException;
import dk.frankbille.scoreboard.ratings.RatingInterface;

public class ELORatingCalculator implements RatingCalculator {
	private Map<Long,RatingInterface> players = new HashMap<Long,RatingInterface>();
	private Map<TeamId,RatingInterface> teams = new HashMap<TeamId,RatingInterface>();
	private Map<Long,ELOGameRating> games = new HashMap<Long,ELOGameRating>();
	private Map<String,ELOGamePlayerRating> gamePlayers = new HashMap<String,ELOGamePlayerRating>();

	public ELORatingCalculator(List<Game> games) {
		setGames(games);
	}

	private void setGames(List<Game> games) {
		//Order the games by date, ascending
		Collections.sort(games, Collections.reverseOrder(new GameComparator()));

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
			throw new RatingException("ELORatingCalculator needs a winning and a losing team");

		//Calculate the rating change for the teams
		double change = ELOCalculator.calculate(winnerRating, winnerScore, loserRating, loserScore);

		if (winnerScore==loserScore && winnerRating>loserRating) {
			GameTeam tempTeam = winner;
			winner = loser;
			loser = tempTeam;

			double tempRating = winnerRating;
			winnerRating = loserRating;
			loserRating = tempRating;
		}

		//Add the rating change to the game and the players
		games.put(game.getId(), new ELOGameRating(winner.getId(), winnerRating, loser.getId(), loserRating, change));
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
	public RatingInterface getPlayerRating(long playerId) {
		//Try to find the playerRating
		RatingInterface player = players.get(playerId);

		if (player==null) {
			//Create a new default player
			player = new ELORating();
			players.put(playerId, player);
		}

		return player;
	}

	public RatingInterface getTeamRating(TeamId teamId) {
		//Try to find the playerRating
		RatingInterface team = teams.get(teamId);

		if (team==null) {
			//Create a new default player
			team = new ELORating();
			teams.put(teamId, team);
		}

		return team;
	}

	@Override
	public double getDefaultRating() {
		return ELOCalculator.DEFAULT_RATING;
	}

	@Override
	public RatingCalculatorType getType() {
		return RatingCalculatorType.ELO;
	}

	private void setRatingChange(GameTeam team, double teamChange) {
		//Find the rating change per player
		double ratingChange = teamChange/team.getTeam().getPlayers().size();

		for (Player player : team.getTeam().getPlayers()) {
			RatingInterface playerRating = getPlayerRating(player.getId());
			addGamePlayerRating(
				team.getGame().getId(), player.getId(),
				playerRating.getRating(), ratingChange);
			playerRating.changeRating(ratingChange);
		}

		RatingInterface teamRating = getTeamRating(new TeamId(team));
		teamRating.changeRating(teamChange);
	}

	public void addGamePlayerRating(long gameId, long playerId, double rating, double change) {
		//Create a new gamePlayer
		gamePlayers.put(gameId+"-"+playerId, new ELOGamePlayerRating(rating, change));
	}

	@Override
	public GamePlayerRatingInterface getGamePlayerRating(long gameId, long playerId) {
		return gamePlayers.get(gameId+"-"+playerId);
	}

	@Override
	public GameRatingInterface getGameRatingChange(Long gameId) {
		return games.get(gameId);
	}
}
