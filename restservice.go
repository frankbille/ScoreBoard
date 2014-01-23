package scoreboard

import (
	"github.com/emicklei/go-restful"
)

type RestService struct {
}

func (rs RestService) Register() {
	// Services that handle individual domains
	gameService := GameService{}
	leagueService := LeagueService{}
	playerService := PlayerService{}
	userService := UserService{}

	// Creation of API paths
	ws := new(restful.WebService)

	ws.
		Path("/api").
		Consumes(restful.MIME_JSON).
		Produces(restful.MIME_JSON)

	// Leagues
	ws.Route(ws.GET("/leagues").To(leagueService.GetAllLeagues).
		Doc("get all leagues, both active and in-active").
		Writes(League{}))

	ws.Route(ws.POST("/leagues").To(leagueService.SaveLeague).
		Doc("save a league, either creating it or updating an existing one").
		Reads(League{}).
		Writes(League{}))

	// Players
	ws.Route(ws.GET("/players").To(playerService.GetAllPlayers).
		Doc("get all players").
		Writes(Player{}))

	ws.Route(ws.POST("/players").To(playerService.SavePlayer).
		Doc("save a player, either creating it or updating an existing one").
		Reads(Player{}).
		Writes(Player{}))

	ws.Route(ws.GET("/players/generateid/{player-name}").To(playerService.GenerateIdForNewPlayer).
		Doc("generate an ID for a player, also saying if it already exists.").
		Param(ws.PathParameter("player-name", "name of the player").DataType("string")).
		Writes(IdResult{}))

	// Games
	ws.Route(ws.GET("/leagues/{league-id}/games").To(gameService.GetLeagueGames).
		Doc("get all games for a specific league").
		Param(ws.PathParameter("league-id", "id of the league to get the games for").DataType("string")).
		Writes(Game{}))

	ws.Route(ws.GET("/players/{player-id}/games").To(gameService.GetPlayerGames).
		Doc("get all games for a specific player").
		Param(ws.PathParameter("player-id", "id of the player to get the games for").DataType("string")).
		Writes(Game{}))

	ws.Route(ws.POST("/leagues/games").To(gameService.SaveGame).
		Doc("create a new game").
		Reads(EditGame{}).
		Writes(Game{}))

	ws.Route(ws.POST("/leagues/{league-id}/games").To(gameService.SaveGame).
		Doc("update an existing game").
		Param(ws.PathParameter("league-id", "id of the league to save a game for").DataType("string")).
		Reads(EditGame{}).
		Writes(Game{}))

	// User
	ws.Route(ws.GET("/user").To(userService.GetUserInfo).
		Doc("get user info").
		Writes(UserInfo{}))

	restful.Add(ws)
}
