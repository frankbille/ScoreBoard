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

function PlayerListController($scope, $resource) {
	var Player = $resource("/api/players/:playerId", {playerId: '@id'});

    var players = Player.query(function() {
    	$scope.players = players;
    });
}

function PlayerDetailController($scope, $resource, $routeParams) {
	var Player = $resource("/api/players/:playerId", {playerId: '@id'});

    $scope.player = Player.get({playerId : $routeParams.playerId});
}

function LeagueListController($scope, $resource) {
	var League = $resource("/api/leagues/:leagueId", {leagueId: '@id'});

    var leagues = League.query(function() {
    	$scope.leagues = leagues;
    });
}

function LeagueDetailController($scope, $resource, $routeParams) {
	var League = $resource("/api/leagues/:leagueId", {leagueId: '@id'});

    $scope.league = League.get({leagueId : $routeParams.leagueId});
}