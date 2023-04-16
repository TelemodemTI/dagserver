DROP TABLE IF EXISTS `sche_properties`;
CREATE TABLE `sche_properties` (
  `prop_id` int NOT NULL AUTO_INCREMENT,
  `prop_name` varchar(190) NOT NULL,
  `prop_group` varchar(190) NOT NULL,
  `prop_value` varchar(190) DEFAULT NULL,
  `prop_description` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`prop_id`)
) ENGINE=InnoDB;



DROP TABLE IF EXISTS `sche_metadata`;
CREATE TABLE `sche_metadata` (
  `metadata_id` int NOT NULL AUTO_INCREMENT,
  `metadata_name` varchar(190) NOT NULL,
  `metadata_host` varchar(190) NOT NULL,
  `metadata_last_updated` datetime DEFAULT NULL,
  PRIMARY KEY (`metadata_id`)
) ENGINE=InnoDB;
