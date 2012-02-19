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
  KEY `FK3BAC568A59F8C5C5` (`game_id`),
  KEY `FK3BAC568AFDDA9E5` (`team_id`),
  CONSTRAINT `FK3BAC568A59F8C5C5` FOREIGN KEY (`game_id`) REFERENCES `game` (`id`),
  CONSTRAINT `FK3BAC568AFDDA9E5` FOREIGN KEY (`team_id`) REFERENCES `team` (`id`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `team_players` (
  `team_id` bigint(20) NOT NULL,
  `player_id` bigint(20) NOT NULL,
  PRIMARY KEY  (`team_id`,`player_id`),
  KEY `FK5AD4FDD0FDDA9E5` (`team_id`),
  KEY `FK5AD4FDD0239384A5` (`player_id`),
  CONSTRAINT `FK5AD4FDD0239384A5` FOREIGN KEY (`player_id`) REFERENCES `player` (`id`),
  CONSTRAINT `FK5AD4FDD0FDDA9E5` FOREIGN KEY (`team_id`) REFERENCES `team` (`id`)
) ENGINE=InnoDB;

