package dk.frankbille.scoreboard.ratings;

public class ELOGamePlayerRating implements GamePlayerRating {
	private double rating;
	private double change;
	
	public ELOGamePlayerRating(double rating, double change) {
		this.rating = rating;
		this.change = change;
	}

	@Override
	public double getRating() {
		return rating;
	}

	@Override
	public double getChange() {
		return change;
	}
}
