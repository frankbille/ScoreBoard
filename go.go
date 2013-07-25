package scoreboard

import (
	"appengine"
	"fmt"
	"github.com/googollee/go-rest"
	"net/http"
	"strings"
)

const (
	SCOREBOARD_DOMAIN string = ".scoreboard.frankbille.dk"
)

func init() {
	handler, err := rest.New(&ScoreBoardService{
		post: make(map[string]string),
	})
	fmt.Errorf("%s", err)
	http.Handle("/api/", handler)

	http.HandleFunc("/api/admin/import", importOldVersion)
	http.HandleFunc("/api/admin/doimport", doImportOldVersion)
}

func GetContext(r *http.Request) appengine.Context {
	c := appengine.NewContext(r)

	host := r.Host

	// Remove potential port
	if strings.Index(host, ":") > -1 {
		host = strings.Split(host, ":")[0]
	}

	if strings.HasSuffix(host, SCOREBOARD_DOMAIN) {
		host = strings.Replace(host, SCOREBOARD_DOMAIN, "", -1)

		// The rest of the host is the namespace
		if len(host) > 0 {
			newContext, err := appengine.Namespace(c, host)
			if err != nil {
				c.Errorf("%v", err)
				return c
			}
			c = newContext
		}
	}

	return c
}

type ScoreBoardService struct {
	rest.Service `prefix:"/api" mime:"application/json" charset:"utf-8"`

	GetAllPlayers  rest.Processor `method:"GET" path:"/players"`
	GetAllLeagues  rest.Processor `method:"GET" path:"/leagues"`
	GetLeagueGames rest.Processor `method:"GET" path:"/leagues/:leagueId/games"`
	SaveGame       rest.Processor `method:"POST" path:"/leagues/:leagueId/games"`
	GetUserInfo    rest.Processor `method:"GET" path:"/user"`

	post map[string]string
}
