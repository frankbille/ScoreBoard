package scoreboard

import (
	"appengine"
	"net/http"
	"strings"
)

const (
	SCOREBOARD_DOMAIN string = "scoreboard.frankbille.dk"
)

func getGaeURL() string {
	if appengine.IsDevAppServer() {
		return "http://localhost:8080"
	} else {
		return "http://" + SCOREBOARD_DOMAIN
	}
}

func init() {
	restService := RestService{}
	restService.Register()

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

	if strings.HasSuffix(host, "."+SCOREBOARD_DOMAIN) {
		host = strings.Replace(host, "."+SCOREBOARD_DOMAIN, "", -1)

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
