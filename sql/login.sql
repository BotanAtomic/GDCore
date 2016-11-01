/*
Navicat MySQL Data Transfer

Source Server         : Graviton
Source Server Version : 50617
Source Host           : localhost:3306
Source Database       : login

Target Server Type    : MYSQL
Target Server Version : 50617
File Encoding         : 65001

Date: 2016-11-01 03:04:59
*/

SET FOREIGN_KEY_CHECKS = 0;
-- ----------------------------
-- Table structure for `accounts`
-- ----------------------------
DROP TABLE IF EXISTS `accounts`;
CREATE TABLE `accounts` (
  `id`                           INT(11)      NOT NULL AUTO_INCREMENT,
  `name`                         VARCHAR(255) NOT NULL,
  `password`                     VARCHAR(255) NOT NULL,
  `nickname`                     VARCHAR(255) NOT NULL,
  `question`                     VARCHAR(255) NOT NULL,
  `answer`                       VARCHAR(255) NOT NULL,
  `rights`                       TINYINT(1)   NOT NULL,
  `banned`                       TINYINT(1)   NOT NULL DEFAULT '0',
  `muted`                        TINYINT(1)   NOT NULL DEFAULT '0',
  `points`                       INT(11)      NOT NULL,
  `channels`                     VARCHAR(255) NOT NULL DEFAULT 'i*#$p%',
  `last_connection`              VARCHAR(0)   NOT NULL,
  `last_address`                 VARCHAR(15)  NOT NULL,
  `friend_notification_listener` TINYINT(1)   NOT NULL,
  PRIMARY KEY (`id`, `name`, `nickname`)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 2
  DEFAULT CHARSET = latin1;

-- ----------------------------
-- Records of accounts
-- ----------------------------
INSERT INTO `accounts` VALUES ('1', 'bobo', 'bobo', 'Salut', 'Hello ?', '', '0', '0', '0', '0', 'i*#$p%', '', '', '0');

-- ----------------------------
-- Table structure for `players`
-- ----------------------------
DROP TABLE IF EXISTS `players`;
CREATE TABLE `players` (
  `id`           INT(11)      NOT NULL,
  `owner`        INT(11)      NOT NULL,
  `name`         VARCHAR(255) NOT NULL,
  `breed`        INT(11)      NOT NULL,
  `sex`          TINYINT(1)   NOT NULL,
  `skin`         SMALLINT(6)  NOT NULL,
  `size`         SMALLINT(6)  NOT NULL,
  `colors`       INT(11)      NOT NULL,
  `level`        SMALLINT(6)  NOT NULL,
  `experience`   BIGINT(20)   NOT NULL,
  `kamas`        BIGINT(20)   NOT NULL,
  `map`          INT(11)      NOT NULL,
  `cell`         SMALLINT(6)  NOT NULL,
  `orientation`  TINYINT(4)   NOT NULL,
  `waypoints`    VARCHAR(255) NOT NULL,
  `stat_points`  SMALLINT(6)  NOT NULL,
  `spell_points` SMALLINT(6)  NOT NULL,
  `energy`       INT(11)      NOT NULL,
  `life`         INT(11)      NOT NULL,
  `vitality`     SMALLINT(6)  NOT NULL,
  `wisdom`       SMALLINT(6)  NOT NULL,
  `strength`     SMALLINT(6)  NOT NULL,
  `intelligence` SMALLINT(6)  NOT NULL,
  `chance`       SMALLINT(6)  NOT NULL,
  `agility`      SMALLINT(6)  NOT NULL,
  `server`       TINYINT(4)   NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `players_name_index` (`name`) USING BTREE,
  KEY `players_owner_id_fk` (`owner`),
  CONSTRAINT `players_ibfk_1` FOREIGN KEY (`owner`) REFERENCES `accounts` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
)
  ENGINE = InnoDB
  DEFAULT CHARSET = latin1;

-- ----------------------------
-- Records of players
-- ----------------------------
INSERT INTO `players` VALUES
  ('1', '1', 'Ok', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '', '0', '0', '0', '0', '0', '0', '0', '0',
   '0', '0', '1');
INSERT INTO `players` VALUES
  ('2', '1', 'oki', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '', '0', '0', '0', '0', '0', '0', '0', '0',
   '0', '0', '1');
INSERT INTO `players` VALUES
  ('3', '1', '', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '', '0', '0', '0', '0', '0', '0', '0', '0', '0',
   '0', '2');

-- ----------------------------
-- Table structure for `servers`
-- ----------------------------
DROP TABLE IF EXISTS `servers`;
CREATE TABLE `servers` (
  `key` VARCHAR(255) DEFAULT NULL
)
  ENGINE = InnoDB
  DEFAULT CHARSET = latin1;

-- ----------------------------
-- Records of servers
-- ----------------------------
INSERT INTO `servers` VALUES ('jiva');
INSERT INTO `servers` VALUES ('test');
