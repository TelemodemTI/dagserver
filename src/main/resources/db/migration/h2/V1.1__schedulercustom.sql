CREATE TABLE sche_events_listeners (
  `LISTENER_NAME` varchar(190) NOT NULL,
  `GROUP_NAME` varchar(190) NOT NULL,
  `ONSTART` varchar(190) DEFAULT NULL,
  `ONEND` varchar(190) DEFAULT NULL,
  `TAG` varchar(190) DEFAULT NULL,
  `JARNAME` varchar(190) DEFAULT NULL
);


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
  `channel` varchar(190)
);

CREATE TABLE `sche_users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(190) NOT NULL,
  `pwdhash` varchar(190) NOT NULL,
  `type_account` varchar(190) NOT NULL,
  `created_at` datetime NOT NULL
);

INSERT INTO `sche_users` VALUES ('1', 'dagserver', 'f399729e48f37737f3aa5f2e74e62f9f75bb7bb10adb065a0a8e21df433d6fa3', 'ADMIN' , CURRENT_TIMESTAMP());

CREATE TABLE `sche_properties` (
  `prop_id` int NOT NULL AUTO_INCREMENT,
  `prop_name` varchar(190) NOT NULL,
  `prop_group` varchar(190) NOT NULL,
  `prop_value` text DEFAULT NULL,
  `prop_description` varchar(500) DEFAULT NULL
);


CREATE TABLE `sche_metadata` (
  `metadata_id` int NOT NULL AUTO_INCREMENT,
  `metadata_name` varchar(190) NOT NULL,
  `metadata_host` varchar(190) NOT NULL,
  `metadata_last_updated` datetime DEFAULT NULL
);

CREATE TABLE `sche_uncompiled_dags` (
  `uncompiled_id` int NOT NULL AUTO_INCREMENT,  
  `uncompiled_name` varchar(190) NOT NULL,
  `bin` text NOT NULL,
  `created_at` datetime NOT NULL
);

commit;