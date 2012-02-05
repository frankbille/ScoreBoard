package dk.frankbille.scoreboard.domain;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Player implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;

	private String name;

	private double rating;

	private double ratingChange;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(id).append(name).toHashCode();
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public double getRating() {
		return rating;
	}

	public void setRatingChange(double change) {
		this.ratingChange = change;
	}

	public double getRatingChange() {
		return ratingChange;
	}
}
