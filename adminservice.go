package scoreboard

import (
	"appengine"
	"appengine/blobstore"
	"appengine/channel"
	"appengine/datastore"
	"appengine/delay"
	"encoding/xml"
	"html/template"
	"io/ioutil"
	"net/http"
	"strconv"
	"strings"
	"time"
	"sort"
)

type Field struct {
	Name  string `xml:"name,attr"`
	Value string `xml:",innerxml"`
}

type Row struct {
	Fields []Field `xml:"field"`
}

type TableData struct {
	Name string `xml:"name,attr"`
	Rows []Row  `xml:"row"`
}

type Database struct {
	Tables []TableData `xml:"table_data"`
}

type MysqlDump struct {
	Databases []Database `xml:"database"`
}

type ImportStatus struct {
	Step   int    `json:"step"`
	Total  int    `json:"total"`
	Status string `json:"status"`
}

type Games struct {
	games []PersistableObject
}

func (g Games) Len() int {
	return len(g.games)
}

func (g Games) Swap(i, j int) {
	g.games[i], g.games[j] = g.games[j], g.games[i]
}

func (g Games) Less(i, j int) bool {
	game1 := g.games[i].(*Game)
	game2 := g.games[j].(*Game)
	
	if game1.GameDate.Before(game2.GameDate) {
		return true
	}
	if game1.GameDate.Equal(game2.GameDate) {
		if game1.Id < game2.Id {
			return true
		}
	}
	
	return false
}

var adminTemplates = template.Must(template.ParseFiles("templates/admin/importoldversionform.html", "templates/admin/importoldversionformprogress.html"))

func importOldVersion(w http.ResponseWriter, r *http.Request) {
	c := appengine.NewContext(r)
	uploadURL, err := blobstore.UploadURL(c, "/api/admin/doimport", nil)
	if err != nil {
		return
	}
	w.Header().Set("Content-Type", "text/html")
	err = adminTemplates.ExecuteTemplate(w, "importoldversionform.html", uploadURL)
	if err != nil {
		c.Errorf("%v", err)
	}
}

func doImportOldVersion(w http.ResponseWriter, r *http.Request) {
	var importFunc = delay.Func("importoldversion", func(c appengine.Context, blobKey appengine.BlobKey) {
		reader := blobstore.NewReader(c, blobKey)

		data, err := ioutil.ReadAll(reader)

		if err != nil {
			c.Errorf("Error: %v", err)
			return
		}

		v := MysqlDump{}

		step := 1
		total := 7

		channel.SendJSON(c, string(blobKey), ImportStatus{
			Step:   step,
			Total:  total,
			Status: "Parsing XML",
		})
		step++

		err = xml.Unmarshal(data, &v)

		if err != nil {
			c.Errorf("Error: %v", err)
			return
		}

		if len(v.Databases) != 1 {
			c.Errorf("Only one database must be present in the dump file")
			return
		}

		type gameData struct {
			GameTeam1 int64
			GameTeam2 int64
			League    int64
		}

		type gameTeamData struct {
			Team  int64
			Score int32
		}

		allPlayers := map[int64]*Player{}
		allLeagues := map[int64]*League{}
		allGames := map[int64]*Game{}
		allGameData := map[int64]gameData{}
		allGameTeamData := map[int64]gameTeamData{}
		allTeamPlayers := map[int64][]int64{}

		tables := v.Databases[0].Tables

		channel.SendJSON(c, string(blobKey), ImportStatus{
			Step:   step,
			Total:  total,
			Status: "Preparing objects",
		})
		step++

		for i := 0; i < len(tables); i++ {
			table := tables[i]
			// Players
			if strings.EqualFold(table.Name, "player") {
				for _, row := range table.Rows {
					var Id int64
					var Name, FullName, GroupName string
					for _, field := range row.Fields {
						if field.Name == "id" {
							Id, _ = strconv.ParseInt(field.Value, 0, 64)
						} else if field.Name == "name" {
							Name = field.Value
						} else if field.Name == "full_name" {
							FullName = field.Value
						} else if field.Name == "group_name" {
							GroupName = field.Value
						}
					}

					p := NewPlayer(Name, FullName, GroupName)
					allPlayers[Id] = &p
				}
			}
			// Leagues
			if strings.EqualFold(table.Name, "league") {
				for _, row := range table.Rows {
					var Id int64
					var Name string
					var Active bool
					for _, field := range row.Fields {
						if field.Name == "id" {
							Id, _ = strconv.ParseInt(field.Value, 0, 64)
						} else if field.Name == "name" {
							Name = field.Value
						} else if field.Name == "active" {
							Active, _ = strconv.ParseBool(field.Value)
						}
					}

					l := NewLeague(Name, Active)
					allLeagues[Id] = &l
				}
			}
			// Games
			if strings.EqualFold(table.Name, "game") {
				for _, row := range table.Rows {
					var Id int64
					var GameDate time.Time
					var GameTeam1 int64
					var GameTeam2 int64
					var LeagueId int64
					for _, field := range row.Fields {
						if field.Name == "id" {
							Id, _ = strconv.ParseInt(field.Value, 0, 64)
						} else if field.Name == "game_date" {
							GameDate, _ = time.Parse("2006-01-02", field.Value)
						} else if field.Name == "team1_id" {
							GameTeam1, _ = strconv.ParseInt(field.Value, 0, 64)
						} else if field.Name == "team2_id" {
							GameTeam2, _ = strconv.ParseInt(field.Value, 0, 64)
						} else if field.Name == "league_id" {
							LeagueId, _ = strconv.ParseInt(field.Value, 0, 64)
						}
					}

					allGames[Id] = &Game{
						Id:       Id,
						GameDate: GameDate,
					}

					allGameData[Id] = gameData{
						GameTeam1: GameTeam1,
						GameTeam2: GameTeam2,
						League:    LeagueId,
					}
				}
			}
			if strings.EqualFold(table.Name, "game_team") {
				for _, row := range table.Rows {
					var Id int64
					var Team int64
					var Score int32
					for _, field := range row.Fields {
						if field.Name == "id" {
							Id, _ = strconv.ParseInt(field.Value, 0, 64)
						} else if field.Name == "team_id" {
							Team, _ = strconv.ParseInt(field.Value, 0, 64)
						} else if field.Name == "score" {
							score, _ := strconv.ParseInt(field.Value, 0, 0)
							Score = int32(score)
						}
					}

					allGameTeamData[Id] = gameTeamData{
						Team:  Team,
						Score: Score,
					}
				}
			}
			if strings.EqualFold(table.Name, "team_players") {
				for _, row := range table.Rows {
					var Team int64
					var Player int64
					for _, field := range row.Fields {
						if field.Name == "team_id" {
							Team, _ = strconv.ParseInt(field.Value, 0, 64)
						} else if field.Name == "player_id" {
							Player, _ = strconv.ParseInt(field.Value, 0, 64)
						}
					}

					players := make([]int64, len(allTeamPlayers[Team])+1)
					copy(players, allTeamPlayers[Team])
					players[len(allTeamPlayers[Team])] = Player
					allTeamPlayers[Team] = players
				}
			}
		}

		// Process objects
		for gameId, game := range allGames {
			gameData := allGameData[gameId]
			gameTeam1 := allGameTeamData[gameData.GameTeam1]
			gameTeam2 := allGameTeamData[gameData.GameTeam2]
			league := allLeagues[gameData.League]

			game.Team1 = createGameTeam(c, gameTeam1.Score, allTeamPlayers[gameTeam1.Team], allPlayers)
			game.Team2 = createGameTeam(c, gameTeam2.Score, allTeamPlayers[gameTeam2.Team], allPlayers)

			game.League = datastore.NewKey(c, "league", league.GetId(), 0, nil)
		}

		// Persist objects
		channel.SendJSON(c, string(blobKey), ImportStatus{
			Step:   step,
			Total:  total,
			Status: "Persist players",
		})
		step++
		players := make([]PersistableObject, len(allPlayers))
		i := 0
		for _, player := range allPlayers {
			players[i] = player
			i++
		}
		persistObjects(c, "player", players)

		channel.SendJSON(c, string(blobKey), ImportStatus{
			Step:   step,
			Total:  total,
			Status: "Persist leagues",
		})
		step++
		leagues := make([]PersistableObject, len(allLeagues))
		i = 0
		for _, league := range allLeagues {
			leagues[i] = league
			i++
		}
		persistObjects(c, "league", leagues)

		channel.SendJSON(c, string(blobKey), ImportStatus{
			Step:   step,
			Total:  total,
			Status: "Persist games",
		})
		step++
		games := make([]PersistableObject, len(allGames))
		i = 0
		for _, game := range allGames {
			games[i] = game
			i++
		}
		sort.Sort(Games{games})
		for index := 0; index < len(games); index++ {
			game := games[index].(*Game)
			game.ChangeDate = time.Now().UnixNano()
		}
		persistObjects(c, "game", games)

		channel.SendJSON(c, string(blobKey), ImportStatus{
			Step:   step,
			Total:  total,
			Status: "Calculate ratings",
		})
		step++
		for _, league := range allLeagues {
			RecalculateLeagueRatings(c, league.GetId())
		}

		err = blobstore.Delete(c, blobKey)

		if err != nil {
			c.Errorf("Error: %v", err)
			return
		}

		channel.SendJSON(c, string(blobKey), ImportStatus{
			Step:   step,
			Total:  total,
			Status: "Finished importing",
		})
	})

	c := appengine.NewContext(r)

	blobs, _, err := blobstore.ParseUpload(r)

	if err != nil {
		c.Errorf("Error: %v", err)
		return
	}

	blobKey := blobs["xmlDumpFile"][0].BlobKey

	// Open channel
	tok, err := channel.Create(c, string(blobKey))
	if err != nil {
		http.Error(w, "Couldn't create Channel", http.StatusInternalServerError)
		c.Errorf("channel.Create: %v", err)
		return
	}

	w.Header().Set("Content-Type", "text/html")
	err = adminTemplates.ExecuteTemplate(w, "importoldversionformprogress.html", map[string]string{
		"token": tok,
	})
	if err != nil {
		c.Errorf("%v", err)
	}

	// Start import
	importFunc.Call(c, blobKey)
}

func createGameTeam(c appengine.Context, score int32, players []int64, allPlayers map[int64]*Player) GameTeam {
	teamPlayers := make([]TeamPlayer, len(players))
	for index, playerId := range players {
		teamPlayers[index] = TeamPlayer{
			Player: allPlayers[playerId].GetId(),
		}
	}
	return GameTeam{
		Players: teamPlayers,
		Score:   score,
	}
}

func persistObjects(c appengine.Context, entityName string, entities []PersistableObject) {
	keys := make([]*datastore.Key, len(entities))

	for i := 0; i < len(entities); i++ {
		entity := entities[i]
		var key *datastore.Key
		if entity.GetId() != "" {
			key = datastore.NewKey(c, entityName, entity.GetId(), 0, entity.GetParent())
		} else {
			key = datastore.NewIncompleteKey(c, entityName, entity.GetParent())
		}
		keys[i] = key
	}

	for i := 0; i < len(entities); i += 500 {
		start := i
		end := i + 500
		if end > len(entities) {
			end = len(entities)
		}
		_, err := datastore.PutMulti(c, keys[start:end], entities[start:end])

		if err != nil {
			c.Errorf("Error: %v", err)
			return
		}
	}
}
