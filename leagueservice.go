package scoreboard

import (
	"appengine"
	"appengine/datastore"
	"fmt"
	"github.com/frankbille/sanitize"
)

func (r ScoreBoardService) HandleGetAllLeagues() []League {
	c := GetContext(r.Request())
	return LoadAllLeagues(c)
}

func leagueIdExists(c appengine.Context, leagueId string) bool {
	league := League{}

	key := datastore.NewKey(c, ENTITY_LEAGUE, leagueId, 0, nil)

	err := datastore.Get(c, key, &league)

	return err == nil
}

func (r ScoreBoardService) HandleSaveLeague(league League) League {
	c := GetContext(r.Request())

	if league.Id == "" {
		generatedId := sanitize.Path(league.Name)
		if leagueIdExists(c, generatedId) {
			return League{
				Name: "LEAGUE ALREADY EXISTS WITH ID: '" + generatedId + "'",
			}
		}
		league.Id = generatedId
	}

	key := datastore.NewKey(c, ENTITY_LEAGUE, league.Id, 0, nil)

	key, err := datastore.Put(c, key, &league)
	if err != nil {
		return League{
			Name: fmt.Sprint(err),
		}
	}

	return league
}
