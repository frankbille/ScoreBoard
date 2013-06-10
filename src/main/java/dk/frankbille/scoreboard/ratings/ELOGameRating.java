package dk.frankbille.scoreboard.ratings;

public class ELOGameRating implements GameRating {
	private double winnerRating;
	private long winnerTeamId;
	private double loserRating;
	private long loserTeamId;
	private double change;

	public ELOGameRating(long winnerTeamId, double winnerRating, long loserTeamId, double loserRating, double change) {
		this.winnerTeamId = winnerTeamId;
		this.winnerRating = winnerRating;
		this.loserTeamId = loserTeamId;
		this.loserRating = loserRating;
		this.change = change;
	}

	@Override
	public double getChange(long teamId) {
		if (winnerTeamId==teamId) {
			return +change;
		}
		else if (loserTeamId==teamId) {
			return -change;
		}
		else {
			throw new IllegalArgumentException(String.format("Team id %d not found in rating", teamId));
		}
	}

	@Override
	public double getRating(long teamId) {
		if (winnerTeamId==teamId) {
			return winnerRating;
		}
		else if (loserTeamId==teamId) {
			return loserRating;
		}
		else {
			throw new IllegalArgumentException(String.format("Team id %d not found in rating", teamId));
		}
	}
}
