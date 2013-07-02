package scoreboard

import (
    "appengine"
    "appengine/datastore"
)

func (r ScoreBoardService) HandleGetAllPlayers() []Player {
    c := appengine.NewContext(r.Request())
    q := datastore.NewQuery("player").Order("Name")
    var players []Player
    _, err := q.GetAll(c, &players)
    if err != nil {
        c.Errorf("Error fetching players: %v", err)
        return nil
    }

    if players == nil {
        return make([]Player, 0, 1)
    }

    return players
}
