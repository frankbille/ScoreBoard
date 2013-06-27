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
		otherwise({
			redirectTo: '/players'
		});
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