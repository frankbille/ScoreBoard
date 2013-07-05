package scoreboard

import (
	"appengine"
	"appengine/blobstore"
	"appengine/datastore"
	"appengine/delay"
	"encoding/xml"
	"html/template"
	"io/ioutil"
	"net/http"
	"strconv"
	"strings"
	"time"
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

var adminTemplates = template.Must(template.ParseFiles("templates/admin/importoldversionform.html"))

func importOldVersion(w http.ResponseWriter, r *http.Request) {
	c := appengine.NewContext(r)
	uploadURL, err := blobstore.UploadURL(c, "/api/admin/doimport", nil)
	if err != nil {
		return
	}
	w.Header().Set("Content-Type", "text/html")
	err = adminTemplates.Execute(w, uploadURL)
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
			Score int
		}

		allPlayers := map[int64]Player{}
		allLeagues := map[int64]League{}
		allGames := map[int64]*Game{}
		allGameData := map[int64]gameData{}
		allGameTeamData := map[int64]gameTeamData{}
		allTeamPlayers := map[int64][]int64{}

		tables := v.Databases[0].Tables

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
					allPlayers[Id] = p
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
					allLeagues[Id] = l
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
					var Score int
					for _, field := range row.Fields {
						if field.Name == "id" {
							Id, _ = strconv.ParseInt(field.Value, 0, 64)
						} else if field.Name == "team_id" {
							Team, _ = strconv.ParseInt(field.Value, 0, 64)
						} else if field.Name == "score" {
							score, _ := strconv.ParseInt(field.Value, 0, 0)
							Score = int(score)
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
		for _, player := range allPlayers {
			persistObject(c, "player", &player)
		}
		for _, league := range allLeagues {
			persistObject(c, "league", &league)
		}
		for _, game := range allGames {
			persistObject(c, "game", game)
		}

		err = blobstore.Delete(c, blobKey)

		if err != nil {
			c.Errorf("Error: %v", err)
			return
		}
	})

	c := appengine.NewContext(r)

	blobs, _, err := blobstore.ParseUpload(r)

	if err != nil {
		c.Errorf("Error: %v", err)
		return
	}

	blobKey := blobs["xmlDumpFile"][0].BlobKey

	importFunc.Call(c, blobKey)
}

func createGameTeam(c appengine.Context, score int, players []int64, allPlayers map[int64]Player) GameTeam {
	playerKeys := make([]string, len(players))
	for index, playerId := range players {
		playerKeys[index] = allPlayers[playerId].GetId()
	}
	return GameTeam{
		Players: playerKeys,
		Score:   score,
	}
}

func persistObject(c appengine.Context, entityName string, entity PersistableObject) {
	var key *datastore.Key
	if entity.GetId() != "" {
		key = datastore.NewKey(c, entityName, entity.GetId(), 0, nil)
	} else {
		key = datastore.NewIncompleteKey(c, entityName, nil)
	}
	key, err := datastore.Put(c, key, entity)

	if err != nil {
		return
	}
}
