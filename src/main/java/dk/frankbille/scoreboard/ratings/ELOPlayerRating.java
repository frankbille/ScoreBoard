package dk.frankbille.scoreboard.ratings;

public class ELOPlayerRating implements PlayerRating {
	private double rating;

	public ELOPlayerRating() {
		this.rating = ELOCalculator.DEFAULT_RATING;
	}

	public double getRating() {
		return rating;
	}

	public void changeRating(double ratingChange) {
		this.rating += ratingChange;
	}

}
