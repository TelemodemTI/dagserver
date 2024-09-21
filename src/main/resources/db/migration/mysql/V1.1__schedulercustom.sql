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
  `evalkey` varchar(190) DEFAULT NULL,
  `dagname` varchar(190) DEFAULT NULL,
  `source_type` varchar(190) DEFAULT NULL,
  `objetive` varchar(190) DEFAULT NULL,
  `exec_dt` datetime DEFAULT NULL,
  `text_value` text,
  `outxcom` text DEFAULT NULL,
  `status` text,
  `marks` text,
  `channel` varchar(190),
  PRIMARY KEY (`log_id`)
) ENGINE=InnoDB;



DROP TABLE IF EXISTS `sche_users`;
CREATE TABLE `sche_users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(190) NOT NULL,
  `pwdhash` varchar(190) NOT NULL,
  `type_account` varchar(190) NOT NULL,
  `created_at` datetime NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB;

-- ----------------------------
-- Records of usuarios
-- ----------------------------
INSERT INTO `sche_users` VALUES ('1', 'dagserver', 'f399729e48f37737f3aa5f2e74e62f9f75bb7bb10adb065a0a8e21df433d6fa3', 'ADMIN' , NOW());


DROP TABLE IF EXISTS `sche_properties`;
CREATE TABLE `sche_properties` (
  `prop_id` int NOT NULL AUTO_INCREMENT,
  `prop_name` varchar(190) NOT NULL,
  `prop_group` varchar(190) NOT NULL,
  `prop_value` text DEFAULT NULL,
  `prop_description` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`prop_id`)
) ENGINE=InnoDB;

INSERT INTO `sche_properties` VALUES ('1','STATUS','RABBIT_PROPS','INACTIVE','rabbit channel status');
INSERT INTO `sche_properties` VALUES ('2','STATUS','REDIS_PROPS','INACTIVE','redis channel status');
INSERT INTO `sche_properties` VALUES ('3','STATUS','KAFKA_PROPS','INACTIVE','kafka channel status');
INSERT INTO `sche_properties` VALUES ('4','STATUS','ACTIVEMQ_PROPS','INACTIVE','activemq channel status');


DROP TABLE IF EXISTS `sche_metadata`;
CREATE TABLE `sche_metadata` (
  `metadata_id` int NOT NULL AUTO_INCREMENT,
  `metadata_name` varchar(190) NOT NULL,
  `metadata_host` varchar(190) NOT NULL,
  `metadata_last_updated` datetime DEFAULT NULL,
  PRIMARY KEY (`metadata_id`)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS `sche_uncompiled_dags`;
CREATE TABLE `sche_uncompiled_dags` (
  `uncompiled_id` int NOT NULL AUTO_INCREMENT,  
  `uncompiled_name` varchar(190) NOT NULL,
  `bin` text NOT NULL,
  `created_at` datetime NOT NULL,
  PRIMARY KEY (`uncompiled_id`)
) ENGINE=InnoDB;

commit;