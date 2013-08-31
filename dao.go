package scoreboard

import (
	"appengine"
	"appengine/datastore"
	"sort"
)

const (
	ENTITY_GAME       string = "game"
	ENTITY_PLAYER     string = "player"
	ENTITY_LEAGUE     string = "league"
	ENTITY_RATING     string = "rating"
	ENTITY_GAMEPLAYER string = "gameplayer"
)

type queryEnhancer func(query *datastore.Query) *datastore.Query

func query(c appengine.Context, entity string, result interface{}, queryEnhancer queryEnhancer) ([]*datastore.Key, error) {
	query := datastore.NewQuery(entity)

	if queryEnhancer != nil {
		query = queryEnhancer(query)
	}

	return query.GetAll(c, result)
}

func PersistObjects(c appengine.Context, keys []*datastore.Key, entities []PersistableObject) []*datastore.Key {
	for i := 0; i < len(entities); i += 500 {
		start := i
		end := i + 500
		if end > len(entities) {
			end = len(entities)
		}
		persistedKeys, err := datastore.PutMulti(c, keys[start:end], entities[start:end])
		if err != nil {
			c.Errorf("Error: %v", err)
			return nil
		}
		
		k := 0
		for j := start; j < end; j++ {
			keys[j] = persistedKeys[k]
			k++
		}
	}
	return keys
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
		game.LeagueId = leagueId
	}

	return games
}

func LoadPlayerGames(c appengine.Context, playerId string) []Game {
	var gamePlayers []GamePlayer
	gpq := datastore.NewQuery(ENTITY_GAMEPLAYER)
	gpq = gpq.Filter("Player =", playerId)
	gpq.GetAll(c, &gamePlayers)
	
	var keys = map[string][]*datastore.Key{}
	
	for i := 0; i < len(gamePlayers); i++ {
		gameKey := gamePlayers[i].Game
		leagueGameKeys := keys[gameKey.Parent().StringID()]
		if leagueGameKeys == nil {
			leagueGameKeys = make([]*datastore.Key, 0, len(gamePlayers))
		}
		keys[gameKey.Parent().StringID()] = append(leagueGameKeys, gameKey)
	}

	var games []Game = make([]Game, len(gamePlayers))
		
	pos := 0
	for _, gameKeys := range keys {
		var leagueGames []Game = make([]Game, len(gameKeys))
		err := datastore.GetMulti(c, gameKeys, leagueGames)
		if err != nil {
			c.Errorf("%v", err)
		}
			
		for i := 0; i < len(leagueGames); i++ {
			game := &leagueGames[i]
			key := gameKeys[i]
			game.Id = key.IntID()
			game.LeagueId = key.Parent().StringID()
		}

		copy(games[pos:], leagueGames)	
		pos += len(gameKeys)
	}
	
	if games == nil {
		games = make([]Game, 0)
	}
	
	sort.Sort(sort.Reverse(SortableGames{games}))

	return games
}
