package scoreboard

import (
	"appengine/datastore"
	"time"
)

type Game struct {
	Id       int64          `json:"id"`
	GameDate time.Time      `json:"gameDate"`
	Team1    GameTeam       `json:"team1"`
	Team2    GameTeam       `json:"team2"`
	League   *datastore.Key `json:"-"`
}

type GameTeam struct {
	// Players is a list of the Player.ID's
	Players []string `json:"players"`
	Score   int      `json:"score"`
}

func (g Game) GetId() string {
	return ""
}
