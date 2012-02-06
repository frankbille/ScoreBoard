CREATE TABLE IF NOT EXISTS `game_team` (
  `id` bigint(20) NOT NULL auto_increment,
  `score` int(11) NOT NULL,
  `game_id` bigint(20) default NULL,
  `team_id` bigint(20) default NULL,
  PRIMARY KEY  (`id`),
  KEY `IDX_GAME_ID` (`game_id`),
  KEY `IDX_TEAM_ID` (`team_id`),
  CONSTRAINT `FK_GAME_TEAM_GAME_ID` FOREIGN KEY (`game_id`) REFERENCES `game` (`id`),
  CONSTRAINT `FK_GAME_TEAM_TEAM_ID` FOREIGN KEY (`team_id`) REFERENCES `team` (`id`)
) ENGINE=InnoDB;
