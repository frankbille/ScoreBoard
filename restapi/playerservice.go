package restapi

import (
    "appengine"
    "appengine/datastore"
    "restapi/domain"
)

func (r ScoreBoardService) HandleGetAllPlayers() []domain.Player {
    c := appengine.NewContext(r.Request())
    q := datastore.NewQuery("player").Order("Name")
    var players []domain.Player
    _, err := q.GetAll(c, &players)
    if err != nil {
        c.Errorf("Error fetching players: %v", err)
        return nil
    }
	
	c.Infof("Players: %v", players)

    if players == nil {
        return make([]domain.Player, 0, 1)
    }

    return players
}
