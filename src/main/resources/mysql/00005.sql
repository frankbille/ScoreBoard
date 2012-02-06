CREATE TABLE IF NOT EXISTS `team_players` (
  `team_id` bigint(20) NOT NULL,
  `player_id` bigint(20) NOT NULL,
  PRIMARY KEY  (`team_id`,`player_id`),
  KEY `IDX_TEAM_ID` (`team_id`),
  KEY `IDX_PLAYER_ID` (`player_id`),
  CONSTRAINT `FK_TEAM_PLAYERS_PLAYER_ID` FOREIGN KEY (`player_id`) REFERENCES `player` (`id`),
  CONSTRAINT `FK_TEAM_PLAYERS_TEAM_ID` FOREIGN KEY (`team_id`) REFERENCES `team` (`id`)
) ENGINE=InnoDB;
