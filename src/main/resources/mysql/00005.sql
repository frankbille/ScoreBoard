CREATE TABLE `user` (
  `username` char(30) NOT NULL,
  `password` char(32) NOT NULL,
  `player_id` bigint(20),
  PRIMARY KEY  (`username`),
  KEY `IDX_PLAYER_ID` (`player_id`),
  CONSTRAINT `FK_USER_PLAYER_ID` FOREIGN KEY (`player_id`) REFERENCES `player` (`id`)
) ENGINE=InnoDB;
