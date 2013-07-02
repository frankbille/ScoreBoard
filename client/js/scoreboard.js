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
    return $resource("/api/players/:playerId", {playerId: '@id'}, {
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

scoreBoardApp.filter("yesno", function() {
	return function(input) {
		return input ? "Yes" : "No";
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

function PlayerListController($scope, PlayerResource) {
    var players = PlayerResource.query(function() {
    	$scope.players = players;
    });
}

function PlayerDetailController($scope, PlayerResource, $routeParams) {
    $scope.player = PlayerResource.get({playerId : $routeParams.playerId});
}

function LeagueListController($scope, LeagueResource) {
    var leagues = LeagueResource.query(function() {
    	$scope.leagues = leagues;
    });
}

function LeagueDetailController($scope, LeagueResource, $routeParams) {
    $scope.league = LeagueResource.get({leagueId : $routeParams.leagueId});
}

function DailyController($scope, LeagueResource, $routeParams) {
	$scope.league = LeagueResource.get({leagueId : $routeParams.leagueId});
}