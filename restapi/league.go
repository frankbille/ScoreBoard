package scoreboard

import (
    "github.com/frankbille/sanitize"
)

type League struct {
    Id     string `json:"id"`
    Name   string `json:"name"`
    Active bool `json:"active"`
}

func NewLeague(Name string, Active bool) League {
    return League{
        Id:     sanitize.Path(Name),
        Name:   Name,
        Active: Active,
    }
}

func (l League) GetId() string {
	return l.Id
}
