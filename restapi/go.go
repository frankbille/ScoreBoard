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
    http.Handle("/", handler)
    fmt.Errorf("%s", err)
}

type ScoreBoardService struct {
    rest.Service `prefix:"/api" mime:"application/json" charset:"utf-8"`

    GetAllPlayers    rest.Processor `method:"GET" path:"/players"`
    ImportOldVersion rest.Processor `method:"GET" path:"/admin/importoldversion"`

    post map[string]string
}
