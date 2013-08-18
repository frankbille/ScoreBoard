var scoreBoardApp = angular.module("ScoreBoard", ["ngResource", "$strap.directives", "ui.select2"])

scoreBoardApp.config(function($routeProvider) {
	$routeProvider.
		when('/players', {
			templateUrl: '/partials/player-list.html',
			controller: PlayerListController
		}).
		when('/players/:playerId', {
			templateUrl: '/partials/player-detail.html',
			controller: PlayerDetailController
		}).
		when('/players/:playerId/edit', {
			templateUrl: '/partials/player-edit.html',
			controller: PlayerEditController
		}).
		when('/addplayer', {
			templateUrl: '/partials/player-edit.html',
			controller: PlayerEditController
		}).
		when('/leagues', {
            templateUrl: '/partials/league-list.html',
            controller: LeagueListController
        }).
		when('/leagues/:leagueId', {
			templateUrl: '/partials/league-detail.html',
			controller: LeagueDetailController
		}).
		when('/leagues/:leagueId/edit', {
			templateUrl: '/partials/league-edit.html',
			controller: LeagueEditController
		}).
		when('/addleague', {
			templateUrl: '/partials/league-edit.html',
			controller: LeagueEditController
		}).
		when('/daily/:leagueId', {
			templateUrl: '/partials/daily.html',
			controller: DailyController
		}).
		when('/daily/:leagueId/:currentPage', {
			templateUrl: '/partials/daily.html',
			controller: DailyController
		}).
		when('/game/new', {
			templateUrl: '/partials/editgame.html',
			controller: EditGameController
		}).
		when('/daily/:leagueId/game/:gameId', {
			templateUrl: '/partials/editgame.html',
			controller: EditGameController
		}).
		otherwise({
			redirectTo: '/players'
		});
});

scoreBoardApp.factory("ScoreBoardCache", function($cacheFactory) {
	return $cacheFactory("ScoreBoard");
});

scoreBoardApp.factory("PlayerResource", function($resource) {
    return $resource("/api/players");
});

scoreBoardApp.factory("LeagueResource", function($resource) {
    return $resource("/api/leagues");
});

scoreBoardApp.factory("GameResource", function($resource, ScoreBoardCache) {
    return $resource("/api/leagues/:leagueId/games", {leagueId: '@id'}, {
		"get"   : {
			method : "GET",
			cache  : ScoreBoardCache
		},
		"query" : {
			method  : "GET",
			cache   : ScoreBoardCache,
			isArray : true
		}
	});
});

scoreBoardApp.factory("UserResource", function($resource) {
    return $resource("/api/user");
});

scoreBoardApp.factory("ServiceFactory", function($q) {
	return {
		create : function(resource, enhanceEntity) {
			return {
				save : function(params, entity) {
					if (angular.isUndefined(entity)) {
						entity = params;
						params = null;
					}
					if (angular.isUndefined(params)) {
						params = null;
					}
					
					var savePromise = resource.save(entity);
					var entityService = this;
					
					return savePromise.$then(function(result) {
						entityService._putEntity(params, result.data);
						return result.data;
					});
				},
				get : function(params, entityId) {
					if (angular.isUndefined(entityId)) {
						entityId = params;
						params = null;
					}
					if (angular.isUndefined(params)) {
						params = null;
					}
					
					var entityMapPromise = this._getEntities(params);
					return entityMapPromise.then(function(entityMap) {
						return entityMap[entityId];
					});
				},
		
				getAll : function(params) {
					if (angular.isUndefined(params)) {
						params = null;
					}
					
					var entityMapPromise = this._getEntities(params);
					return entityMapPromise.then(function(entityMap) {
						var entityList = [];
				
						for (var entityId in entityMap) {
							entityList.push(entityMap[entityId]);
						}
				
						return entityList;
					});
				},
		
				_entities : null,
				_getEntities : function(params) {
					var deferredEntityMap = $q.defer();
					
					if (this._entities == null) {
						this._entities = {};
					}
					
					var dataKey = "empty";
					if (params != null) {
						dataKey = params.dataKey;
					}
					
					if (this._entities[dataKey] == null) {
						var entityService = this;
					    var entities = resource.query(params, function() {
							entityService._entities[dataKey] = {};
							for (i = 0; i < entities.length; i++) {
								entityService._putEntity(params, entities[i]);
							}
							deferredEntityMap.resolve(entityService._entities[dataKey]);					
					    });
					} else {
						deferredEntityMap.resolve(this._entities[dataKey]);
					}
			
					return deferredEntityMap.promise;
				},
				
				_putEntity : function(params, entity) {
					var dataKey = "empty";
					if (params != null) {
						dataKey = params.dataKey;
					}
					
					if (angular.isFunction(enhanceEntity)) {
						enhanceEntity(entity);
					}
					this._entities[dataKey][entity.id] = entity;
				}
			}
		}
	};
});

scoreBoardApp.factory("PlayerService", function(PlayerResource, ServiceFactory) {
	return ServiceFactory.create(PlayerResource);
});

scoreBoardApp.factory("LeagueService", function(LeagueResource, ServiceFactory) {
	return ServiceFactory.create(LeagueResource);
});

scoreBoardApp.factory("GameService", function(GameResource, ServiceFactory) {
	return ServiceFactory.create(GameResource, function(game) {
		game.getWinner = function() {
			if (game.team1.score >= game.team2.score) {
				return game.team1;
			} else {
				return game.team2;
			}
		};
		
		game.getLoser = function() {
			if (game.team1.score < game.team2.score) {
				return game.team1;
			} else {
				return game.team2;
			}
			
		};
	});
});

scoreBoardApp.filter("yesno", function() {
	return function(input) {
		return input ? "Yes" : "No";
	}
});

scoreBoardApp.filter("startFrom", function() {
    return function(array, start) {
		if (!angular.isArray(array)) return array;
        start = +start; //parse to int
        return array.slice(start);
    }
});

scoreBoardApp.directive("gamelist", function() {
	return {
		restrict: 'A',
		templateUrl: '/partials/game-list.html',
		replace: false,
		transclude: false,
		scope: {
			loadGames: "&",
			location: "@",
			currentPage: "="
		},
		controller: function($scope, $element, $attrs, $transclude, PlayerService) {
			$scope.currentPage = $scope.currentPage || 1;
			$scope.pageSize = 20;

			$scope.loadGames({
				callback: function(games, players) {
					games.sort(function(p1, p2) {
						var d1 = new Date(p1.gameDate);
						var d2 = new Date(p2.gameDate);
						return d2 < d1 ? -1 : d1 < d2 ? 1 : 0;
					});

					$scope.games = games;

					$scope.pageCount = Math.ceil($scope.games.length / $scope.pageSize);
					$scope.pages = [];
					for (var i = 1; i <= $scope.pageCount; i++) {
						$scope.pages.push({
							number: i
						});
					}
				}
			});
		}
	}
});

scoreBoardApp.directive("gameteam", function() {
	return {
		restrict: 'A',
		templateUrl: '/partials/gameteam.html',
		replace: false,
		transclude: false,
		scope: {
			gameteam: "="
		},
		controller: function($scope, $element, $attrs, $transclude, PlayerService) {
			$scope.teamPlayers = $scope.gameteam.players;
			$scope.avgStartRating = 0
			$scope.avgEndRating = 0;
			for (i = 0; i < $scope.teamPlayers.length; i++) {
				var teamPlayer = $scope.teamPlayers[i];
				teamPlayer.playerObject = PlayerService.get(teamPlayer.player);
				if (teamPlayer.endRating > teamPlayer.startRating) {
					teamPlayer.rateDirection = "up text-success";
				} else if (teamPlayer.endRating < teamPlayer.startRating) {
					teamPlayer.rateDirection = "down text-error";
				} else {
					teamPlayer.rateDirection = "right text-info";
				}
				$scope.avgStartRating += teamPlayer.startRating;
				$scope.avgEndRating += teamPlayer.endRating;
			}
			$scope.avgStartRating /= $scope.teamPlayers.length;
			$scope.avgEndRating /= $scope.teamPlayers.length;
			if ($scope.avgEndRating > $scope.avgStartRating) {
				$scope.rateDirection = "up text-success";
			} else if ($scope.avgEndRating < $scope.avgStartRating) {
				$scope.rateDirection = "down text-error";
			} else {
				$scope.rateDirection = "right text-info";
			}
		}
	}
});
 
scoreBoardApp.directive('ngBlur', ['$parse', function($parse) {
  return function(scope, element, attr) {
    var fn = $parse(attr['ngBlur']);
    element.bind('blur', function(event) {
      scope.$apply(function() {
        fn(scope, {$event:event});
      });
    });
  }
}]);

function MenuController($rootScope, $scope, UserResource) {
	var userInfo = UserResource.get(function() {
		$scope.userInfoReceived = true;
		$rootScope.loggedIn = userInfo.loggedIn;
		$scope.loginLogoutUrl = userInfo.loginLogoutUrl;
		$scope.userEmail = userInfo.email;
		if (userInfo.loggedIn) {
			$scope.loginLogoutText = "Logout";
		} else {
			$scope.loginLogoutText = "Login";
		}
	});
}

function DailyMenuController($scope, LeagueResource) {
    var leagues = LeagueResource.query(function() {
        $scope.leagues = leagues;
    });
}

function PlayerListController($scope, PlayerService) {
	PlayerService.getAll().then(function(playerList) {
		$scope.players = playerList;
	});
}

function PlayerDetailController($scope, PlayerService, $routeParams) {
	PlayerService.get($routeParams.playerId).then(function(player) {
		$scope.player = player;
	});
}

function PlayerEditController($scope, PlayerService, $routeParams, $http, $location) {
	$scope.player = {};
	
	if (angular.isUndefined($routeParams.playerId)) {
		$scope.creating = true;
	} else {
		$scope.creating = false;
		$scope.loading = true;
		PlayerService.get($routeParams.playerId).then(function(player) {
			$scope.player = player;
			$scope.viewid = player.id;
			
			$scope.loading = false;
		});	
	}
	
	$scope.checkId = function() {
		if ($scope.creating && angular.isString($scope.player.name) && $scope.player.name.length > 0) {
			$scope.validId = false;
			$scope.invalidId = false;
			$scope.checkingId = true;
			$http.get("/api/players/generateid/"+$scope.player.name).success(function(data) {
				$scope.viewid = data.generatedId;
				$scope.validId = !data.alreadyExists;
				$scope.invalidId = data.alreadyExists;
				$scope.checkingId = false;
			});
		}
	};
	
	$scope.save = function() {
		$scope.saving = true;
		PlayerService.save($scope.player).then(function(player) {
			$scope.saving = false;
			$location.path("/players/"+player.id);
		});
	};
}

function LeagueListController($scope, LeagueService) {
	LeagueService.getAll().then(function(leagues) {
    	$scope.leagues = leagues;
	});
}

function LeagueDetailController($scope, LeagueService, $routeParams) {
	LeagueService.get($routeParams.leagueId).then(function(league) {
		$scope.league = league;
	});
}

function LeagueEditController($scope, LeagueService, $routeParams, $http, $location) {
	$scope.league = {
		active: true
	};
	
	if (angular.isUndefined($routeParams.leagueId)) {
		$scope.creating = true;
	} else {
		$scope.creating = false;
		$scope.loading = true;
		LeagueService.get($routeParams.leagueId).then(function(league) {
			$scope.league = league;
			
			$scope.loading = false;
		});	
	}
	
	$scope.save = function() {
		$scope.saving = true;
		LeagueService.save($scope.league).then(function(league) {
			$scope.saving = false;
			$location.path("/leagues/"+league.id);
		});
	};
}

function handleTeam(team, playerStatMap, playerMap, winner) {
	for (var i = 0; i < team.players.length; i++) {
		var teamPlayer = team.players[i];
		var player = playerMap[teamPlayer.player];

		var playerStat = playerStatMap[player.id];
		if (angular.isUndefined(playerStat)) {
			playerStat = {
				id: player.id,
				name: player.name,
				rating: teamPlayer.endRating,
				gamesWon: 0,
				gamesLost: 0,
				trend: []
			};
			playerStatMap[player.id] = playerStat;
		}
	
		if (winner) {
			playerStat.gamesWon++;
		} else {
			playerStat.gamesLost++;
		}
		
		playerStat.trend.push(winner ? "icon-double-angle-up text-success" : "icon-double-angle-down text-error");
	}
}

function DailyController($scope, LeagueService, PlayerService, GameService, $routeParams) {
	LeagueService.get($routeParams.leagueId).then(function(league) {
		$scope.league = league;
	});
	
	$scope.currentPage = $routeParams.currentPage;
	
	$scope.allGames = function(callback) {
		GameService.getAll({leagueId : $routeParams.leagueId, dataKey : $routeParams.leagueId}).then(function(games) {
			PlayerService.getAll().then(function(players) {
				callback(games, players);
				
				var playerMap = {};
				for (var i = 0; i < players.length; i++) {
					playerMap[players[i].id] = players[i];
				}

				var playerStatMap = {};
				for (var i = 0; i < games.length; i++) {
					var game = games[i];
					handleTeam(game.getWinner(), playerStatMap, playerMap, true);
					handleTeam(game.getLoser(), playerStatMap, playerMap, false);
				}

				var playerList = $.map(playerStatMap, function(k, v) {
					return k;
				});

				playerList.sort(function(p1, p2) {
					return p2.rating-p1.rating;
				});

				if (playerList.length > 0) {
					playerList[0].medal = "icon-trophy icon-large gold";
				}
				if (playerList.length > 1) {
					playerList[1].medal = "icon-trophy icon-large silver";
				}
				if (playerList.length > 2) {
					playerList[2].medal = "icon-trophy icon-large bronze";
				}

				$scope.players = playerList;
			});
		});
	};
}

function EditGameController($scope, LeagueService, PlayerService, GameService, $routeParams, $location) {
	if (angular.isUndefined($routeParams.gameId)) {
		$scope.editGame = "Add";
		$scope.game = {
			gameDate: new Date(),
			team1: {
				score: 0
			},
			team2: {
				score: 0
			}
		};
	} else {
		$scope.editGame = "Edit";
		GameService.get({leagueId : $routeParams.leagueId, dataKey : $routeParams.leagueId}, $routeParams.gameId).then(function(game) {
			$scope.game = {
				id: +$routeParams.gameId,
				gameDate: new Date(game.gameDate),
				league: $routeParams.leagueId,
				team1: {
					score: game.team1.score,
					players: $.map(game.team1.players, function(k, v) {return k.player})
				},
				team2: {
					score: game.team2.score,
					players: $.map(game.team2.players, function(k, v) {return k.player})
				}
			};
		});
	}
	
	LeagueService.getAll().then(function(leagues) {
		$scope.leagues = leagues;
	});
	
	PlayerService.getAll().then(function(players) {
		$scope.players = players;
	});
	
	$scope.save = function() {
		$scope.saving = true;
		
		GameService.save({leagueId : $routeParams.leagueId, dataKey : $routeParams.leagueId}, $scope.game).then(function(game) {
			$location.path("/daily/"+$routeParams.leagueId);
		});
	};
}
