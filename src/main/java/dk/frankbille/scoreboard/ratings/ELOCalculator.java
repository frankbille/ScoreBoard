package dk.frankbille.scoreboard.ratings;

public class ELOCalculator {
	public static double DEFAULT_RATING = 1000;
	private static double K_FACTOR = 50; //Max +25 rating points for a win (and -25 for losing - giving the sum of 50 points)
	private static double RATING_FACTOR = 400; //Rating +400 means 10 times as good
	private static double SCORE_PERCENT = 50; //Smallest winning margin will give at least 50% of the K_FACTOR

	public static double calculate(double winnerRating, int winnerScore,
			double loserRating, int loserScore) {
		//See the formula at http://en.wikipedia.org/wiki/Elo_rating_system#Mathematical_details

		//Expected win ration (0.50 = 50% chance of winning)
		double expected = Math.pow(10, winnerRating/RATING_FACTOR);
		expected = expected/(expected+Math.pow(10, loserRating/RATING_FACTOR));

		final double winMargin;
		final double maxRatingPoints;
		if (winnerScore>loserScore) {
			//Max rating point that can be earned (Highest win margin will give 100% of K_FACTOR)
			winMargin = ((double)(winnerScore-loserScore))/winnerScore;
			maxRatingPoints = winMargin*K_FACTOR*(SCORE_PERCENT/100)+K_FACTOR*(100-SCORE_PERCENT)/100;
		}
		else {
			//The game was drawn, give 25% of the max score to the lowest rated team
			if (winnerRating>loserRating) {
				//"winner" has the lowest rating
				maxRatingPoints = K_FACTOR*(100-SCORE_PERCENT)/100/2;
			}
			else if (winnerRating<loserRating) {
				//"loser" has the lowest rating
				maxRatingPoints = -K_FACTOR*(100-SCORE_PERCENT)/100/2;
			}
			else {
				//Same rating
				winMargin = 0;
				maxRatingPoints = 0;
			}
		}

		//Calculate the actual rating gain for the winner
		return maxRatingPoints*(1-expected);
	}
}
