package dk.frankbille.scoreboard.ratings;

public class RatingProvider {
	public static RatingCalculator ratings;
	public static RatingCalculator getRatings() {
		if (ratings==null)
			ratings = new ELORatingCalculator();
		return ratings;
	}
}
