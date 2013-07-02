package scoreboard

import (
    "appengine"
    "appengine/datastore"
)

func (r ScoreBoardService) HandleGetAllLeagues() []League {
    c := appengine.NewContext(r.Request())
    q := datastore.NewQuery("league").Order("Name")
    var leagues []League
    _, err := q.GetAll(c, &leagues)
    if err != nil {
        c.Errorf("Error fetching leagues: %v", err)
        return nil
    }

    if leagues == nil {
        return make([]League, 0, 1)
    }

    return leagues
}
