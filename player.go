package scoreboard

import (
	"appengine/datastore"
	"github.com/frankbille/sanitize"
)

type Player struct {
	Id        string `json:"id"`
	Name      string `json:"name"`
	FullName  string `json:"fullName"`
	GroupName string `json:"groupName"`
}

func NewPlayer(Name, FullName, GroupName string) Player {
	return Player{
		Id:        sanitize.Path(Name),
		Name:      Name,
		FullName:  FullName,
		GroupName: GroupName,
	}
}

func (p Player) GetId() string {
	return p.Id
}

func (p Player) GetParent() *datastore.Key {
	return nil
}
