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
		Collections.sort(games, new GameComparator());
		
		//Go through the games one-by-one
		for (Game game : games) {
			addTeamRatings(game);
		}
	}
	private void addTeamRatings(Game game) throws RatingException {
		//Find the teams
		List<GameTeam> teams = game.getTeams();
		if (teams.size()!=2)
			throw new RatingException("ELORatingCalculator currently only support 2 team matches");
		
		//Calculate the teams before-ratings
		GameTeam winner = null;
		int winnerScore = 0;
		double winnerRating = ELOCalculator.DEFAULT_RATING;
		
		GameTeam loser = null;
		int loserScore = 0;
		double loserRating = ELOCalculator.DEFAULT_RATING;

		for (GameTeam team:teams)
			if (game.didTeamWin(team)) {
				winner = team;
				winnerRating = calculateTeamRating(team);
				winnerScore = team.getScore();
			}
			else {
				loser = team;
				loserRating = calculateTeamRating(team);
				loserScore = team.getScore();
			}
		
		//Check that we have 2 teams
		if (winner==null ||loser==null)
			throw new RatingException("ELORatingCalculator needs a winning and a loosing team");
		
		//Calculate the rating change for the team
		double change = ELOCalculator.calculate(winnerRating, winnerScore, loserRating, loserScore);
		
		//Add the rating change to the game and the players
		games.put(game.getId(), new ELOGameRating(winnerRating, loserRating, change));
		setRatingChange(game,winner,+change);
		setRatingChange(game,loser,-change);
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

	private void setRatingChange(Game game, GameTeam team, double ratingChange) {
		//Find the rating change per player
		ratingChange = ratingChange/team.getTeam().getPlayers().size();
		
		for (Player player : team.getTeam().getPlayers()) {
			ELOPlayerRating playerRating = getPlayerRating(player.getId());
			addGamePlayerRating(
				game.getId(), player.getId(), 
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
