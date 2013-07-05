package scoreboard

import (
	"appengine"
)

func (r ScoreBoardService) HandleGetLeagueGames() []Game {
	c := appengine.NewContext(r.Request())
	return LoadLeagueGames(c, r.Vars()["leagueId"])
}
