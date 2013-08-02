package scoreboard

import (
	"appengine"
	"appengine/datastore"
	"fmt"
	"github.com/frankbille/sanitize"
)

func (r ScoreBoardService) HandleGetAllPlayers() []Player {
	c := GetContext(r.Request())
	return LoadAllPlayers(c)
}

type IdResult struct {
	GeneratedId   string `json:"generatedId"`
	AlreadyExists bool   `json:"alreadyExists"`
}

func playerIdExists(c appengine.Context, playerId string) bool {
	player := Player{}

	key := datastore.NewKey(c, ENTITY_PLAYER, playerId, 0, nil)

	err := datastore.Get(c, key, &player)
	
	return err == nil
}

func (r ScoreBoardService) HandleGenerateIdForNewPlayer() IdResult {
	c := GetContext(r.Request())

	playerName := r.Vars()["playerName"]
	generatedId := sanitize.Path(playerName)
	alreadyExists := playerIdExists(c, generatedId)

	return IdResult{
		GeneratedId:   generatedId,
		AlreadyExists: alreadyExists,
	}
}

func (r ScoreBoardService) HandleSavePlayer(player Player) Player {
	c := GetContext(r.Request())
	
	if player.Id == "" {
		generatedId := sanitize.Path(player.Name)
		if playerIdExists(c, generatedId) {
			return Player{
				Name: "PLAYER ALREADY EXISTS WITH ID: '"+generatedId+"'",
			}
		}
		player.Id = generatedId	
	}
	
	key := datastore.NewKey(c, ENTITY_PLAYER, player.Id, 0, nil)
	
	key, err := datastore.Put(c, key, &player)
	if err != nil {
		return Player {
			Name: fmt.Sprint(err),
		}
	}
	
	return player
}
