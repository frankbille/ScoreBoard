package scoreboard

import (
	"appengine/datastore"
)

type PersistableObject interface {
	GetId() string
	GetParent() *datastore.Key
}
