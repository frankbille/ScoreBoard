package dk.frankbille.scoreboard.ratings;

public class ELOPlayerRating {
	private long id;
	private double rating;
	
	public ELOPlayerRating(long id, double rating) {
		this.id = id;
		this.rating = rating;
	}

	public long getId() {
		return id;
	}
	
	public double getRating() {
		return rating;
	}
	
	public int hashCode() {
		return (int)id;
	}

	public boolean equals(Object obj) {
		return (obj instanceof ELOPlayerRating) && (((ELOPlayerRating)obj).getId()==this.id); 
	}

	public void changeRating(double change) {
		rating += change; 
	}
}
