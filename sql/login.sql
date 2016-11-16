/*
Navicat MySQL Data Transfer

Source Server         : Graviton
Source Server Version : 50617
Source Host           : localhost:3306
Source Database       : login

Target Server Type    : MYSQL
Target Server Version : 50617
File Encoding         : 65001

Date: 2016-11-23 18:56:00
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `accounts`
-- ----------------------------
DROP TABLE IF EXISTS `accounts`;
CREATE TABLE `accounts` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `nickname` varchar(255) NOT NULL,
  `question` varchar(255) NOT NULL,
  `answer` varchar(255) NOT NULL,
  `rights` tinyint(1) NOT NULL,
  `banned` tinyint(1) NOT NULL DEFAULT '0',
  `muted` tinyint(1) NOT NULL DEFAULT '0',
  `points` int(11) NOT NULL,
  `channels` varchar(255) NOT NULL DEFAULT 'i*#$p%',
  `last_connection` varchar(255) NOT NULL,
  `last_address` varchar(15) NOT NULL,
  `friend_notification_listener` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`,`name`,`nickname`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of accounts
-- ----------------------------
INSERT INTO `accounts` VALUES ('1', 'bobo', 'bobo', 'Salut', 'Hello ?', 'dont now', '0', '0', '0', '0', 'i*#$p%', '2016~11~23~17~55', '127.0.0.1', '0');
INSERT INTO `accounts` VALUES ('2', 'bobo2', 'bobo', 'SalutC', '', '', '0', '0', '0', '0', 'i*#$p%', '', '', '0');

-- ----------------------------
-- Table structure for `players`
-- ----------------------------
DROP TABLE IF EXISTS `players`;
CREATE TABLE `players` (
  `id` int(11) NOT NULL,
  `owner` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `breed` tinyint(11) NOT NULL,
  `sex` tinyint(1) NOT NULL,
  `skin` smallint(6) NOT NULL,
  `size` smallint(6) NOT NULL DEFAULT '100',
  `colors` varchar(255) NOT NULL,
  `level` smallint(6) NOT NULL DEFAULT '1',
  `experience` bigint(20) NOT NULL DEFAULT '0',
  `title` tinyint(6) DEFAULT '0',
  `kamas` bigint(20) NOT NULL DEFAULT '0',
  `map` int(11) NOT NULL DEFAULT '0',
  `cell` smallint(6) NOT NULL DEFAULT '0',
  `orientation` tinyint(4) NOT NULL DEFAULT '1',
  `waypoints` varchar(255) DEFAULT NULL,
  `stat_points` smallint(6) NOT NULL DEFAULT '0',
  `spell_points` smallint(6) NOT NULL DEFAULT '0',
  `energy` smallint(11) NOT NULL DEFAULT '10000',
  `life` tinyint(11) NOT NULL DEFAULT '100',
  `vitality` smallint(6) NOT NULL DEFAULT '0',
  `wisdom` smallint(6) NOT NULL DEFAULT '0',
  `strength` smallint(6) NOT NULL DEFAULT '0',
  `intelligence` smallint(6) NOT NULL DEFAULT '0',
  `chance` smallint(6) NOT NULL DEFAULT '0',
  `agility` smallint(6) NOT NULL DEFAULT '0',
  `server` tinyint(4) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `players_name_index` (`name`) USING BTREE,
  KEY `players_owner_id_fk` (`owner`),
  CONSTRAINT `players_ibfk_1` FOREIGN KEY (`owner`) REFERENCES `accounts` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of players
-- ----------------------------
INSERT INTO `players` VALUES ('1', '1', 'Yywo', '4', '0', '40', '100', '-1;-1;-1', '1', '0', '0', '0', '10300', '323', '1', null, '0', '0', '10000', '100', '0', '0', '0', '0', '0', '0', '1');

-- ----------------------------
-- Table structure for `servers`
-- ----------------------------
DROP TABLE IF EXISTS `servers`;
CREATE TABLE `servers` (
  `id` tinyint(11) DEFAULT NULL,
  `key` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of servers
-- ----------------------------
INSERT INTO `servers` VALUES ('1', 'jiva');
INSERT INTO `servers` VALUES ('2', 'test');
