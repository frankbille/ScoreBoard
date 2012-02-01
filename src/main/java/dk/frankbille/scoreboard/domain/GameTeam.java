package dk.frankbille.scoreboard.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="game_team")
public class GameTeam implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;

	private Game game;

	private Team team;

	private int score;
	
	private double rating;

	private double ratingChange;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@OneToOne
    @JoinColumn(name="game_id")
	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	@OneToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="team_id")
	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public void setRating(double rating) {
		this.rating = rating;		
	}

	public double getRating() {
		return rating;
	}

	public void setRatingChange(double ratingChange) {
		this.ratingChange = ratingChange;
	}

	public double getRatingChange() {
		return ratingChange;
	}

}
