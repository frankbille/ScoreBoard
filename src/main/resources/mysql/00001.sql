CREATE TABLE IF NOT EXISTS `game` (
  `id` bigint(20) NOT NULL auto_increment,
  `date` date default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB;
