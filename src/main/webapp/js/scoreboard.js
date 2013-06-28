var scoreBoardApp = angular.module("ScoreBoard", ["ngResource"])

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
    $scope.items = [
        {
            name : "Add Game",
            icon : "icon-plus",
            link : "/addgame"
        },
        {
            name : "Daily",
            link : "/daily"
        },
        {
            name : "Players",
            link : "/players"
        },
        {
            name : "Leagues",
            link : "/leagues"
        }
    ];
}

function PlayerListController($scope, $resource, PlayerResource) {
    var players = PlayerResource.query(function() {
    	$scope.players = players;
    });
}

function PlayerDetailController($scope, $resource, PlayerResource, $routeParams) {
    $scope.player = PlayerResource.get({playerId : $routeParams.playerId});
}

function LeagueListController($scope, $resource, LeagueResource) {
    var leagues = LeagueResource.query(function() {
    	$scope.leagues = leagues;
    });
}

function LeagueDetailController($scope, $resource, LeagueResource, $routeParams) {
    $scope.league = LeagueResource.get({leagueId : $routeParams.leagueId});
}