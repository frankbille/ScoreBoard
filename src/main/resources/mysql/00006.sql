ALTER TABLE
	game
		ADD COLUMN team1_id BIGINT(20),
		ADD COLUMN team2_id BIGINT(20),
		ADD INDEX `IDX_TEAM1_ID` (`team1_id`),
		ADD INDEX `IDX_TEAM2_ID` (`team2_id`),
		ADD CONSTRAINT `FK_GAME_TEAM1_GAME_TEAM_ID` FOREIGN KEY (`team1_id`) REFERENCES `game_team` (`id`),
		ADD CONSTRAINT `FK_GAME_TEAM2_GAME_TEAM_ID` FOREIGN KEY (`team2_id`) REFERENCES `game_team` (`id`);
		
UPDATE
	game
SET
	team1_id = (SELECT id FROM game_team WHERE game_id=game.id LIMIT 1),
	team2_id = (SELECT id FROM game_team WHERE game_id=game.id LIMIT 1,1);
