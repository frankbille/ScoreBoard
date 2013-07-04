package scoreboard

import (
	"appengine"
	"appengine/datastore"
)

func (r ScoreBoardService) HandleGetLeagueGames() []Game {
	c := appengine.NewContext(r.Request())
	leagueKey := datastore.NewKey(c, "league", r.Vars()["leagueId"], 0, nil)
	q := datastore.NewQuery("game").Filter("League =", leagueKey).Order("-GameDate")
	var games []Game
	_, err := q.GetAll(c, &games)
	if err != nil {
		c.Errorf("Error fetching games: %v", err)
		return nil
	}

	if games == nil {
		return make([]Game, 0, 1)
	}

	return games
}
