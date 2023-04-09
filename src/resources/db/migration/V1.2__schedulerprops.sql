DROP TABLE IF EXISTS `sche_properties`;
CREATE TABLE `sche_properties` (
  `prop_id` int NOT NULL AUTO_INCREMENT,
  `prop_name` varchar(190) NOT NULL,
  `prop_group` varchar(190) NOT NULL,
  `prop_value` varchar(190) DEFAULT NULL,
  `prop_description` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`prop_id`)
) ENGINE=InnoDB;

