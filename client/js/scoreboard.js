var scoreBoardApp = angular.module("ScoreBoard", ["ngResource", "ui.bootstrap"])

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
    return $resource("/api/leagues/:leagueId", {leagueId: '@id'}, {
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
		create : function(resource) {
			return {
				get : function(entityId) {
					var entityMapPromise = this._getEntities();
					return entityMapPromise.then(function(entityMap) {
						return entityMap[entityId];
					});
				},
		
				getAll : function() {
					var entityMapPromise = this._getEntities();
					return entityMapPromise.then(function(entityMap) {
						var entityList = [];
				
						for (var entityId in entityMap) {
							entityList.push(entityMap[entityId]);
						}
				
						return entityList;
					});
				},
		
				_entities : null,
				_getEntities : function() {
					var deferredEntityMap = $q.defer();
			
					if (this._entities == null) {
						var entityService = this;
					    var entities = resource.query(function() {
							entityService._entities = {};
							for (i = 0; i < entities.length; i++) {
								var entity = entities[i];
								entityService._entities[entity.id] = entity;
							}
							deferredEntityMap.resolve(entityService._entities);					
					    });
					} else {
						deferredEntityMap.resolve(this._entities);
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
			$scope.players = [];
			for (i = 0; i < $scope.gameteam.players.length; i++) {
				$scope.players.push(PlayerService.get($scope.gameteam.players[i]));
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

function DailyController($scope, LeagueService, GameResource, $routeParams) {
	LeagueService.get($routeParams.leagueId).then(function(league) {
		$scope.league = league;
	});
	
	var games = GameResource.query({leagueId : $routeParams.leagueId}, function() {
		$scope.games = games;
		$scope.pageCount = Math.ceil($scope.games.length / $scope.pageSize);
	})
	$scope.currentPage = 1;
	$scope.pageSize = 10;
}