package restapi

import (
    "fmt"
    "github.com/googollee/go-rest"
    "net/http"
)

func init() {
    handler, err := rest.New(&ScoreBoardService{
        post: make(map[string]string),
    })
    fmt.Errorf("%s", err)
    http.Handle("/api/", handler)

    http.HandleFunc	("/api/admin/import", importOldVersion)
}

type ScoreBoardService struct {
    rest.Service `prefix:"/api" mime:"application/json" charset:"utf-8"`

    GetAllPlayers rest.Processor `method:"GET" path:"/players"`

    post map[string]string
}
