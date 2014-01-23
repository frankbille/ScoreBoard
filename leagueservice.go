package scoreboard

import (
	"appengine"
	"appengine/datastore"
	"github.com/emicklei/go-restful"
	"net/http"
)

type LeagueService struct {
}

func (ls LeagueService) GetAllLeagues(request *restful.Request, response *restful.Response) {
	c := GetContext(request.Request)
	response.WriteEntity(LoadAllLeagues(c))
}

func (ls LeagueService) SaveLeague(request *restful.Request, response *restful.Response) {
	c := GetContext(request.Request)

	league := League{}
	err := request.ReadEntity(&league)
	if err == nil {
		if league.Id == "" {
			l := NewLeague(league.Name, league.Active)
			if leagueIdExists(c, l.Id) {
				response.WriteServiceError(http.StatusInternalServerError, restful.NewError(http.StatusBadRequest, "LEAGUE ALREADY EXISTS WITH ID: '"+l.Id+"'"))
				return
			}
			league.Id = l.Id
		}

		key := datastore.NewKey(c, ENTITY_LEAGUE, league.Id, 0, nil)

		key, err := datastore.Put(c, key, &league)
		if err != nil {
			response.WriteError(http.StatusInternalServerError, err)
		} else {
			response.WriteEntity(league)
		}
	} else {
		response.WriteError(http.StatusInternalServerError, err)
	}
}

func leagueIdExists(c appengine.Context, leagueId string) bool {
	league := League{}

	key := datastore.NewKey(c, ENTITY_LEAGUE, leagueId, 0, nil)

	err := datastore.Get(c, key, &league)

	return err == nil
}