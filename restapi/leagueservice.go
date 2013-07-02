package restapi

import (
    "appengine"
    "appengine/datastore"
    "restapi/domain"
)

func (r ScoreBoardService) HandleGetAllLeagues() []domain.League {
    c := appengine.NewContext(r.Request())
    q := datastore.NewQuery("league").Order("Name")
    var leagues []domain.League
    _, err := q.GetAll(c, &leagues)
    if err != nil {
        c.Errorf("Error fetching leagues: %v", err)
        return nil
    }

    if leagues == nil {
        return make([]domain.League, 0, 1)
    }

    return leagues
}
