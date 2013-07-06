package scoreboard

import (
	"appengine"
	"appengine/datastore"
)

const (
	ENTITY_GAME   string = "game"
	ENTITY_PLAYER string = "player"
	ENTITY_LEAGUE string = "league"
)

type queryEnhancer func(query *datastore.Query)

func query(c appengine.Context, entity string, result interface{}, queryEnhancer queryEnhancer) {
	query := datastore.NewQuery(entity)

	if queryEnhancer != nil {
		queryEnhancer(query)
	}

	_, err := query.GetAll(c, result)

	if err != nil {
		c.Errorf("Error fetching objects: %v", err)
	}
}

func LoadAllPlayers(c appengine.Context) []Player {
	var players []Player

	sort := func(query *datastore.Query) {
		query.Order("Name")
	}

	query(c, ENTITY_PLAYER, &players, sort)
	
	if players == nil {
		players = []Player{}
	}

	return players
}

func LoadAllLeagues(c appengine.Context) []League {
	var leagues []League

	sort := func(query *datastore.Query) {
		query.Order("Name")
	}

	query(c, ENTITY_LEAGUE, &leagues, sort)

	return leagues
}

func LoadLeagueGames(c appengine.Context, leagueId string) []Game {
	var games []Game

	sortAndFilter := func(query *datastore.Query) {
		leagueKey := datastore.NewKey(c, ENTITY_LEAGUE, leagueId, 0, nil)
		query.Filter("League =", leagueKey).Order("-GameDate")
	}

	query(c, ENTITY_GAME, &games, sortAndFilter)

	return games
}
