CREATE TABLE IF NOT EXISTS `game` (
  `id` bigint(20) NOT NULL auto_increment,
  `date` date default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `player` (
  `id` bigint(20) NOT NULL auto_increment,
  `name` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `team` (
  `id` bigint(20) NOT NULL auto_increment,
  `name` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB;

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

CREATE TABLE IF NOT EXISTS `team_players` (
  `team_id` bigint(20) NOT NULL,
  `player_id` bigint(20) NOT NULL,
  PRIMARY KEY  (`team_id`,`player_id`),
  KEY `IDX_TEAM_ID` (`team_id`),
  KEY `IDX_PLAYER_ID` (`player_id`),
  CONSTRAINT `FK_TEAM_PLAYERS_PLAYER_ID` FOREIGN KEY (`player_id`) REFERENCES `player` (`id`),
  CONSTRAINT `FK_TEAM_PLAYERS_TEAM_ID` FOREIGN KEY (`team_id`) REFERENCES `team` (`id`)
) ENGINE=InnoDB;

