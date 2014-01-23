package scoreboard

import (
	"appengine"
	"github.com/emicklei/go-restful"
	"github.com/emicklei/go-restful/swagger"
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

	// Optionally, you can install the Swagger Service which provides a nice Web UI on your REST API
	// You need to download the Swagger HTML5 assets and change the FilePath location in the config below.
	// Open <your_app_id>.appspot.com/apidocs and enter http://<your_app_id>.appspot.com/apidocs.json in the api input field.
	config := swagger.Config{
		WebServices:    restful.RegisteredWebServices(), // you control what services are visible
		WebServicesUrl: getGaeURL(),
		ApiPath:        "/apidocs.json",

		// Optionally, specifiy where the UI is located
		SwaggerPath: "/apidocs/",
		// GAE support static content which is configured in your app.yaml.
		// This example expect the swagger-ui in static/swagger so you should place it there :)
		SwaggerFilePath: "static/swagger"}
	swagger.InstallSwaggerService(config)
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
