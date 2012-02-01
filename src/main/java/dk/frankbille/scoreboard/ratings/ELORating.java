package dk.frankbille.scoreboard.ratings;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dk.frankbille.scoreboard.comparators.GameComparator;
import dk.frankbille.scoreboard.domain.Game;
import dk.frankbille.scoreboard.domain.GameTeam;
import dk.frankbille.scoreboard.domain.Player;

public class ELORating {
	public static double DEFAULT_RATING = 1000; 
	private double K_FACTOR;
	private double RATING_FACTOR;
	private double SCORE_PERCENT;
	private Set<ELOPlayerRating> playerRatings;
	
	public ELORating() {
		this.K_FACTOR = 50; //Max +25 rating points for a win (and -25 for losing - giving the sum of 50 points)
		this.RATING_FACTOR = 400; //Rating +400 means 10 times as good
		this.SCORE_PERCENT = 50; //Smallest winning margin will give at least 50% of the K_FACTOR
		
		playerRatings = new HashSet<ELOPlayerRating>();
	}
	public void calculateRatings(List<Game> games) {
		//Order the games by date
		Collections.sort(games, new GameComparator());
		
		//Go through the games one-by-one
		for (Game game : games) {
			try {
				addTeamRatings(game);
			} catch (RatingException e) {
				e.printStackTrace();
			}
		}
	}
	private void addTeamRatings(Game game) throws RatingException {
		System.out.println("-----------------------------------------------------");
		System.out.println("Game: "+game.getId());
		
		//Find the teams
		List<GameTeam> teams = game.getTeams();
		if (teams.size()!=2)
			throw new RatingException("ELORating currently only support 2 team matches");
		
		//Calculate the teams before-ratings
		GameTeam winner = null;
		int winnerScore = 0;
		GameTeam loser = null;
		int loserScore = 0;
		for (GameTeam team:teams)
			if (game.didTeamWin(team)) {
				System.out.println("Winner team:");
				winner = team;
				calculateTeamRating(team);
				winnerScore = team.getScore();
				System.out.println("Winner team rating: "+winner.getRating());
			}
			else {
				System.out.println("Loser team:");
				loser = team;
				calculateTeamRating(team);
				loserScore = team.getScore();
				System.out.println("Loser team rating: "+loser.getRating());
			}
		
		//Check that we have 2 teams
		if (winner==null ||loser==null)
			throw new RatingException("ELORating needs a winning and a loosing team");
		
		//Calculate the rating change for the team
		double ratingChange = calculateRatingChange(winner.getRating(), winnerScore, loser.getRating(), loserScore);
		System.out.println("Rating change: "+ratingChange);
		
		//Add the rating change to the game and the players
		setRatingChange(winner,+ratingChange);
		setRatingChange(loser,-ratingChange);
	}

	private void setRatingChange(GameTeam team, double ratingChange) {
		team.setRatingChange(ratingChange);
		
		//Find the rating change per player
		ratingChange = ratingChange/team.getTeam().getPlayers().size();
		
		for (Player player : team.getTeam().getPlayers()) {
			ELOPlayerRating playerRating = getPlayerRating(player.getId());
			player.setRatingChange(ratingChange);
			playerRating.changeRating(ratingChange);
		}
	}
	private double calculateRatingChange(double winnerRating, int winnerScore,
			double loserRating, int loserScore) {
		//See the formula at http://en.wikipedia.org/wiki/Elo_rating_system#Mathematical_details
		
		//Expected win ration (0.50 = 50% chance of winning)
		double expected = Math.pow(10, winnerRating/RATING_FACTOR);
		expected = expected/(expected+Math.pow(10, loserRating/RATING_FACTOR));
		
		//Max rating point that can be earned (Highest win margin will give 100% of K_FACTOR)
		double winMargin = ((double)(winnerScore-loserScore))/winnerScore; 
		double maxRatingPoints = winMargin*K_FACTOR*(SCORE_PERCENT/100)+K_FACTOR*(100-SCORE_PERCENT)/100;
		
		//Calculate the actual rating gain for the winner
		return maxRatingPoints*(1-expected);
	}
	private void calculateTeamRating(GameTeam team) {
		ELOTeamRatingBuilder ratingBuilder = new ELOTeamRatingBuilder(DEFAULT_RATING); 
		for (Player player : team.getTeam().getPlayers()) {
			ELOPlayerRating playerRating = getPlayerRating(player.getId());

			player.setRating(playerRating.getRating());
			ratingBuilder.addPlayer(playerRating.getRating());
			
			System.out.println(player.getName()+": "+playerRating.getRating());
		}
		
		team.setRating(ratingBuilder.getTeamRating());
	}
	
	private ELOPlayerRating getPlayerRating(long playerId) {
		//Try to find the playerRating
		for(ELOPlayerRating playerRating : playerRatings) {
			if (playerRating.getId()==playerId)
				return playerRating;
		}
		
		ELOPlayerRating playerRating = new ELOPlayerRating(playerId,DEFAULT_RATING);
		playerRatings.add(playerRating);
		return playerRating;
	}
}
