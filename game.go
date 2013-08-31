package scoreboard

import (
	"appengine/datastore"
	"time"
)

type Game struct {
	Id         int64          `datastore:"-" json:"id"`
	GameDate   time.Time      `json:"gameDate"`
	ChangeDate int64          `json:"changeDate"`
	Team1      GameTeam       `json:"team1"`
	Team2      GameTeam       `json:"team2"`
	League     *datastore.Key `json:"-"`
	LeagueId   string         `datastore:"-" json:"leagueId"`
}

func (g Game) GetId() string {
	return ""
}

func (g Game) GetParent() *datastore.Key {
	return g.League
}

func (g Game) HasPlayer(playerId string) bool {
	return g.Team1.HasPlayer(playerId) || g.Team2.HasPlayer(playerId)
}

type GameTeam struct {
	// Players is a list of the Player.ID's
	Players []TeamPlayer `json:"players"`
	Score   int32        `json:"score"`
}

func (gt GameTeam) HasPlayer(playerId string) bool {
	for i := 0; i < len(gt.Players); i++ {
		if gt.Players[i].Player == playerId {
			return true
		}
	}

	return false
}

type TeamPlayer struct {
	Player      string  `json:"player"`
	StartRating float64 `json:"startRating"`
	EndRating   float64 `json:"endRating"`
}

// GamePlayer is a cache table for quickly finding the games that a player has played
type GamePlayer struct {
	Game   *datastore.Key
	Player string
}

func (gp GamePlayer) GetId() string {
	return ""
}

func (gp GamePlayer) GetParent() *datastore.Key {
	return gp.Game
}

// Sorting
type SortableGames struct {
	games []Game
}

func (g SortableGames) Len() int {
	return len(g.games)
}

func (g SortableGames) Swap(i, j int) {
	g.games[i], g.games[j] = g.games[j], g.games[i]
}

func (g SortableGames) Less(i, j int) bool {
	game1 := g.games[i]
	game2 := g.games[j]
	
	if game1.GameDate.Before(game2.GameDate) {
		return true
	}
	if game1.GameDate.Equal(game2.GameDate) {
		if game1.ChangeDate < game2.ChangeDate {
			return true
		}
	}
	
	return false
}
