package domain

import (
    "appengine/datastore"
    "time"
)

type Game struct {
    Id       int64          `json:"id"`
    GameDate time.Time      `json:"gameDate"`
    Team1    GameTeam       `json:"team1"`
    Team2    GameTeam       `json:"team2"`
    League   *datastore.Key `json:"league"`
}

type GameTeam struct {
    Players []*datastore.Key `json:"players"`
    Score   int              `json:"score"`
}

func (g Game) GetId() string {
    return ""
}
