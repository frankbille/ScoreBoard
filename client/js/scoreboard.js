var scoreBoardApp = angular.module("ScoreBoard", ["ngResource", "$strap.directives"])

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
		when('/leagues', {
            templateUrl: '/partials/league-list.html',
            controller: LeagueListController
        }).
		when('/leagues/:leagueId', {
			templateUrl: '/partials/league-detail.html',
			controller: LeagueDetailController
		}).
		when('/daily/:leagueId', {
			templateUrl: '/partials/daily.html',
			controller: DailyController
		}).
		when('/daily/:leagueId/:currentPage', {
			templateUrl: '/partials/daily.html',
			controller: DailyController
		}).
		otherwise({
			redirectTo: '/players'
		});
});

scoreBoardApp.factory("ScoreBoardCache", function($cacheFactory) {
	return $cacheFactory("ScoreBoard");
});

scoreBoardApp.factory("PlayerResource", function($resource, ScoreBoardCache) {
    return $resource("/api/players");
});

scoreBoardApp.factory("LeagueResource", function($resource, ScoreBoardCache) {
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

scoreBoardApp.factory("ServiceFactory", function($q) {
	return {
		create : function(resource, enhanceEntity) {
			return {
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
								var entity = entities[i];
								if (angular.isFunction(enhanceEntity)) {
									enhanceEntity(entity);
								}
								entityService._entities[dataKey][entity.id] = entity;
							}
							deferredEntityMap.resolve(entityService._entities[dataKey]);					
					    });
					} else {
						deferredEntityMap.resolve(this._entities[dataKey]);
					}
			
					return deferredEntityMap.promise;
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

function MenuController($scope, $location) {
    $scope.location = $location;
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

	$scope.currentPage = $routeParams.currentPage || 1;
	$scope.pageSize = 20;
	GameService.getAll({leagueId : $routeParams.leagueId, dataKey : $routeParams.leagueId}).then(function(games) {
		// Ensure that players has been loaded
		PlayerService.getAll().then(function(players) {
			$scope.games = games;
			$scope.pageCount = Math.ceil($scope.games.length / $scope.pageSize);
			$scope.pages = [];
			for (var i = 1; i <= $scope.pageCount; i++) {
				$scope.pages.push({
					number: i
				});
			}
			
			var playerMap = {};
			for (var i = 0; i < players.length; i++) {
				playerMap[players[i].id] = players[i];
			}
		
			var playerStatMap = {};
			for (var i = 0; i < $scope.games.length; i++) {
				var game = $scope.games[i];
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
}