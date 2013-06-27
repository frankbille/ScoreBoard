var scoreBoardApp = angular.module("ScoreBoard", ["ngResource"]).config(['$routeProvider', function($routeProvider) {
	$routeProvider.
		when('/players', {
			templateUrl: 'partials/player-list.html',
			controller: PlayerListController
		}).
		when('/players/:playerId', {
			templateUrl: 'partials/player-detail.html',
			controller: PlayerDetailController
		}).
		otherwise({
			redirectTo: '/players'
		});
}]);

function PlayerListController($scope, $resource) {
	var Player = $resource("/api/players/:playerId", {playerId: '@id'});

    var players = Player.query(function() {
        console.log(players);
    	$scope.players = players;
    });
}

function PlayerDetailController($scope, $resource, $routeParams) {
	var Player = $resource("/api/players/:playerId", {playerId: '@id'});

    $scope.player = Player.get({playerId : $routeParams.playerId});
}