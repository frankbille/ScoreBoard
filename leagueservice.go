package scoreboard

import (
	"appengine"
)

func (r ScoreBoardService) HandleGetAllLeagues() []League {
	c := appengine.NewContext(r.Request())
	return LoadAllLeagues(c)
}
