package scoreboard

import (
	"appengine"
	"appengine/datastore"
	"github.com/emicklei/go-restful"
	"net/http"
)

type PlayerService struct {
}

type IdResult struct {
	GeneratedId   string `json:"generatedId"`
	AlreadyExists bool   `json:"alreadyExists"`
}

func (ps PlayerService) GetAllPlayers(request *restful.Request, response *restful.Response) {
	c := GetContext(request.Request)
	response.WriteEntity(LoadAllPlayers(c))
}

func (ps PlayerService) SavePlayer(request *restful.Request, response *restful.Response) {
	c := GetContext(request.Request)

	player := Player{}
	err := request.ReadEntity(&player)
	if err == nil {
		if player.Id == "" {
			p := NewPlayer(player.Name, player.FullName, player.GroupName)
			if playerIdExists(c, p.Id) {
				response.WriteServiceError(http.StatusInternalServerError, restful.NewError(http.StatusBadRequest, "PLAYER ALREADY EXISTS WITH ID: '"+p.Id+"'"))
				return
			}
			player.Id = p.Id
		}

		key := datastore.NewKey(c, ENTITY_PLAYER, player.Id, 0, nil)

		key, err := datastore.Put(c, key, &player)
		if err != nil {
			response.WriteError(http.StatusInternalServerError, err)
		} else {
			response.WriteEntity(player)
		}
	} else {
		response.WriteError(http.StatusInternalServerError, err)
	}
}

func (ps PlayerService) GenerateIdForNewPlayer(request *restful.Request, response *restful.Response) {
	c := GetContext(request.Request)

	playerName := request.PathParameter("player-name")
	player := NewPlayer(playerName, "", "")
	alreadyExists := playerIdExists(c, player.Id)

	response.WriteEntity(IdResult{
		GeneratedId:   player.Id,
		AlreadyExists: alreadyExists,
	})
}

func playerIdExists(c appengine.Context, playerId string) bool {
	player := Player{}

	key := datastore.NewKey(c, ENTITY_PLAYER, playerId, 0, nil)

	err := datastore.Get(c, key, &player)

	return err == nil
}
