package dk.frankbille.scoreboard.ratings;

public class ELOTeamRatingBuilder {
	private double rating;
	private int players;
	
	public ELOTeamRatingBuilder() {
		this.rating = 0;
		this.players = 0;
	}
	
	public void addPlayer(double rating) {
		this.rating += rating;
		this.players++;
	}

	public double getTeamRating() {
		if (players==0)
			return ELOCalculator.DEFAULT_RATING;
		else
			return rating/players;
	}
}
