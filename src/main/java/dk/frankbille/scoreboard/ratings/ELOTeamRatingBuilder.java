package dk.frankbille.scoreboard.ratings;

import java.util.ArrayList;
import java.util.List;

public class ELOTeamRatingBuilder {
	private double defaultRating;
	
	private List<Double> playerRatings;

	public ELOTeamRatingBuilder(double defaultRating) {
		this.defaultRating = defaultRating;
		playerRatings = new ArrayList<Double>();
	}

	public void addPlayer(double rating) {
		playerRatings.add(rating);
	}

	public double getTeamRating() {
		if (playerRatings.size()==0)
			return defaultRating;
		
		double teamRating = 0;
		for (Double rating : playerRatings) {
			teamRating += rating;
		}
		return teamRating/playerRatings.size();
	}

}
