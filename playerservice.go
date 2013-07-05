package scoreboard

import (
	"appengine"
)

func (r ScoreBoardService) HandleGetAllPlayers() []Player {
	c := appengine.NewContext(r.Request())
	return LoadAllPlayers(c)
}
