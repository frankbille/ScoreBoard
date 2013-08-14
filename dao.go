package scoreboard

import (
	"appengine"
	"appengine/datastore"
)

const (
	ENTITY_GAME   string = "game"
	ENTITY_PLAYER string = "player"
	ENTITY_LEAGUE string = "league"
	ENTITY_RATING string = "rating"
)

type queryEnhancer func(query *datastore.Query) *datastore.Query

func query(c appengine.Context, entity string, result interface{}, queryEnhancer queryEnhancer) ([]*datastore.Key, error) {
	query := datastore.NewQuery(entity)

	if queryEnhancer != nil {
		query = queryEnhancer(query)
	}

	return query.GetAll(c, result)
}

func LoadAllPlayers(c appengine.Context) []Player {
	var players []Player

	sort := func(query *datastore.Query) *datastore.Query {
		return query.Order("Name")
	}

	query(c, ENTITY_PLAYER, &players, sort)

	if players == nil {
		players = []Player{}
	}

	return players
}

func LoadAllLeagues(c appengine.Context) []League {
	var leagues []League

	sort := func(query *datastore.Query) *datastore.Query {
		return query.Order("Name")
	}

	query(c, ENTITY_LEAGUE, &leagues, sort)

	if leagues == nil {
		leagues = []League{}
	}

	return leagues
}

func LoadLeagueGames(c appengine.Context, leagueId string) []Game {
	var games []Game

	sortAndFilter := func(query *datastore.Query) *datastore.Query {
		leagueKey := datastore.NewKey(c, ENTITY_LEAGUE, leagueId, 0, nil)
		return query.Ancestor(leagueKey).Order("-GameDate").Order("-ChangeDate")
	}

	keys, err := query(c, ENTITY_GAME, &games, sortAndFilter)
	if err != nil {
		c.Errorf("%v", err)
	}
	
	if games == nil {
		games = make([]Game, 0)
	}

	for i := 0; i < len(games); i++ {
		game := &games[i]
		key := keys[i]
		game.Id = key.IntID()
	}

	return games
}
