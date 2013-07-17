package scoreboard

import (
	"appengine/datastore"
	"time"
)

type Game struct {
	Id         int64          `datastore:"-" json:"id"`
	GameDate   time.Time      `json:"gameDate"`
	ChangeDate time.Time      `json:"-"`
	Team1      GameTeam       `json:"team1"`
	Team2      GameTeam       `json:"team2"`
	League     *datastore.Key `json:"-"`
}

func (g Game) GetId() string {
	return ""
}

func (g Game) GetParent() *datastore.Key {
	return g.League
}

type GameTeam struct {
	// Players is a list of the Player.ID's
	Players []TeamPlayer `json:"players"`
	Score   int32        `json:"score"`
}

type TeamPlayer struct {
	Player      string  `json:"player"`
	StartRating float64 `json:"startRating"`
	EndRating   float64 `json:"endRating"`
}
