package scoreboard

import (
	"appengine"
	"appengine/datastore"
)

func (r ScoreBoardService) HandleGetLeagueGames() []Game {
	c := appengine.NewContext(r.Request())
	return LoadLeagueGames(c, r.Vars()["leagueId"])
}

func (r ScoreBoardService) HandleRecalculateLeagueRatings() {
	c := appengine.NewContext(r.Request())
	RecalculateLeagueRatings(c, r.Vars()["leagueId"])
}

func RecalculateLeagueRatings(c appengine.Context, leagueId string) {
	leagueKey := datastore.NewKey(c, ENTITY_LEAGUE, leagueId, 0, nil)
	err := datastore.RunInTransaction(c, func(c appengine.Context) error {
		gamesQuery := datastore.NewQuery(ENTITY_GAME).Ancestor(leagueKey).Order("GameDate").Order("ChangeDate")
		var games []Game
		gameKeys, err := gamesQuery.GetAll(c, &games)
		if err != nil {
			return err
		}

		playerRatings := map[string]float64{}

		for i := 0; i < len(games); i++ {
			game := games[i]
			team1Score := game.Team1.Score
			team2Score := game.Team2.Score

			team1Rating := getTeamRating(game.Team1.Players, playerRatings)
			team2Rating := getTeamRating(game.Team2.Players, playerRatings)

			var resultRating float64
			if team1Score > team2Score {
				resultRating = CalculateRating(team1Rating, team2Rating, team1Score, team2Score)
				applyResultRating(c, &game.Team1, playerRatings, resultRating, true)
				applyResultRating(c, &game.Team2, playerRatings, resultRating, false)
			} else {
				resultRating = CalculateRating(team2Rating, team1Rating, team2Score, team1Score)
				applyResultRating(c, &game.Team2, playerRatings, resultRating, true)
				applyResultRating(c, &game.Team1, playerRatings, resultRating, false)
			}
		}

		_, err = datastore.PutMulti(c, gameKeys, games)
		if err != nil {
			return err
		}

		return nil
	}, nil)

	if err != nil {
		c.Errorf("%v", err)
	}
}

func getTeamRating(teamPlayers []TeamPlayer, playerRatings map[string]float64) float64 {
	var teamRating float64

	for i := 0; i < len(teamPlayers); i++ {
		teamPlayer := teamPlayers[i]
		playerRating, found := playerRatings[teamPlayer.Player]
		if found == false {
			playerRating = 1000
			playerRatings[teamPlayer.Player] = playerRating
		}

		teamRating += playerRating
	}

	return teamRating / float64(len(teamPlayers))
}

func applyResultRating(c appengine.Context, gameTeam *GameTeam, playerRatings map[string]float64, resultRating float64, winner bool) {
	teamPlayers := gameTeam.Players
	
	perPlayerRating := resultRating / float64(len(teamPlayers))

	for i := 0; i < len(teamPlayers); i++ {
		teamPlayer := &teamPlayers[i]
		startRating := playerRatings[teamPlayer.Player]
		var endRating float64
		if winner {
			endRating = startRating + perPlayerRating
		} else {
			endRating = startRating - perPlayerRating
		}
		playerRatings[teamPlayer.Player] = endRating

		teamPlayer.StartRating = startRating
		teamPlayer.EndRating = endRating
	}
}
