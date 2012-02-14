package dk.frankbille.scoreboard.ratings;

public class ELOGameRating implements GameRating {
	private double winner;
	private double loser;
	private double change;
	
	public ELOGameRating(double winner, double loser, double change) {
		this.winner = winner;
		this.loser = loser;
		this.change = change;			
	}
	
	
	@Override
	public double getWinnerRating() {
		return winner;
	}

	@Override
	public double getLoserRating() {
		return loser;
	}

	@Override
	public double getChange() {
		return change;
	}
}
