package scoreboard

import (
	"math"
)

const (
	DEFAULT_RATING float64 = 1000
	K_FACTOR       float64 = 50  // Max +25 rating points for a win (and -25 for losing - giving the sum of 50 points)
	RATING_FACTOR  float64 = 400 // Rating +400 means 10 times as good
	SCORE_PERCENT  float64 = 50  // Smallest winning margin will give at least 50% of the K_FACTOR
)

// See the formula at http://en.wikipedia.org/wiki/Elo_rating_system#Mathematical_details
func CalculateRating(winnerRating, loserRating float64, winnerScore, loserScore int32) float64 {
	// Expected win ration (0.50 = 50% chance of winning)
	expected := math.Pow(10, winnerRating/RATING_FACTOR)
	expected = expected / (expected + math.Pow(10, loserRating/RATING_FACTOR))

	var maxRatingPoints float64

	if winnerScore > loserScore {
		// Max rating point that can be earned (Highest win margin will give 100% of K_FACTOR)
		winMargin := float64((float64(winnerScore) - float64(loserScore)) / float64(winnerScore))
		maxRatingPoints = winMargin*K_FACTOR*(SCORE_PERCENT/100) + K_FACTOR*(100-SCORE_PERCENT)/100
	} else {
		//The game was drawn, give 25% of the max score to the lowest rated team
		switch {
		case winnerRating > loserRating:
			//"winner" has the lowest rating
			maxRatingPoints = K_FACTOR * (100 - SCORE_PERCENT) / 100 / 2
		case winnerRating < loserRating:
			//"loser" has the lowest rating
			maxRatingPoints = -K_FACTOR * (100 - SCORE_PERCENT) / 100 / 2
		default:
			maxRatingPoints = 0
		}
	}

	return maxRatingPoints * (1 - expected)
}
