package restapi

import (
	"fmt"
	"net/http"
	"restapi/domain"
	"github.com/googollee/go-rest"
	"appengine"
	"appengine/datastore"
)

func init() {
	handler, err := rest.New(&ScoreBoardService{
		post:  make(map[string]string),
	})
	http.Handle("/", handler)
	fmt.Errorf("%s", err)
}

type ScoreBoardService struct {
	rest.Service `prefix:"/api" mime:"application/json" charset:"utf-8"`

	GetAllPlayers rest.Processor `method:"GET" path:"/players"`

	post  map[string]string
}

func (r ScoreBoardService) HandleGetAllPlayers() []domain.Player {
	c := appengine.NewContext(r.Request());
	q := datastore.NewQuery("Player").
		Order("Name")
	var players []domain.Player
	_, err := q.GetAll(c, &players)
	if err != nil {
		c.Errorf("fetching players: %v", err)
		return nil
	}

	return players
}

