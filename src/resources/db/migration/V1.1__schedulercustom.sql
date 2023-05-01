DROP TABLE IF EXISTS SCHE_EVENTS_LISTENERS;

DROP TABLE IF EXISTS `sche_events_listeners`;
CREATE TABLE `sche_events_listeners` (
  `LISTENER_NAME` varchar(190) NOT NULL,
  `GROUP_NAME` varchar(190) NOT NULL,
  `ONSTART` varchar(190) DEFAULT NULL,
  `ONEND` varchar(190) DEFAULT NULL,
  PRIMARY KEY (`LISTENER_NAME`)
) ENGINE=InnoDB;



DROP TABLE IF EXISTS `sche_logs`;
CREATE TABLE `sche_logs` (
  `log_id` int NOT NULL AUTO_INCREMENT,
  `dagname` varchar(190) DEFAULT NULL,
  `exec_dt` datetime DEFAULT NULL,
  `text_value` text,
  `outxcom` text,
  `status` text,
  PRIMARY KEY (`log_id`)
) ENGINE=InnoDB;



DROP TABLE IF EXISTS `sche_users`;
CREATE TABLE `sche_users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(190) NOT NULL,
  `pwdhash` varchar(190) NOT NULL,
  `created_at` datetime NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB;

-- ----------------------------
-- Records of usuarios
-- ----------------------------
INSERT INTO `sche_users` VALUES ('1', 'dagserver', 'f399729e48f37737f3aa5f2e74e62f9f75bb7bb10adb065a0a8e21df433d6fa3', NOW());


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



commit;