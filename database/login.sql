-- MySQL dump 10.13  Distrib 5.5.54, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: login
-- ------------------------------------------------------
-- Server version	5.5.54-0+deb8u1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `accounts`
--

DROP TABLE IF EXISTS `accounts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
  `friends` varchar(100) NOT NULL,
  `enemies` varchar(100) NOT NULL,
  PRIMARY KEY (`id`,`name`,`nickname`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `accounts`
--

LOCK TABLES `accounts` WRITE;
/*!40000 ALTER TABLE `accounts` DISABLE KEYS */;
INSERT INTO `accounts` VALUES (2,'bobo2','bobo','SalutC','','',0,0,0,0,'i*#$p%','','',0,'','');
/*!40000 ALTER TABLE `accounts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `players`
--

DROP TABLE IF EXISTS `players`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
  `alignment` int(11) NOT NULL DEFAULT '0',
  `honor` int(11) NOT NULL DEFAULT '0',
  `dishonnor` int(11) NOT NULL DEFAULT '0',
  `pvp_enabled` tinyint(4) NOT NULL DEFAULT '0',
  `guild` varchar(100) NOT NULL DEFAULT '0',
  `savedLocation` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `players_name_index` (`name`) USING BTREE,
  KEY `players_owner_id_fk` (`owner`),
  CONSTRAINT `players_ibfk_1` FOREIGN KEY (`owner`) REFERENCES `accounts` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `players`
--

LOCK TABLES `players` WRITE;
/*!40000 ALTER TABLE `players` DISABLE KEYS */;
INSERT INTO `players` VALUES (1,2,'Peshmerga',9,0,90,100,'-1;-1;-1',1,0,0,0,10326,448,5,'',0,0,10000,100,0,0,0,0,0,0,1,0,0,0,0,'0','10292;284');
/*!40000 ALTER TABLE `players` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `servers`
--

DROP TABLE IF EXISTS `servers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `servers` (
  `id` tinyint(11) DEFAULT NULL,
  `key` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `servers`
--

LOCK TABLES `servers` WRITE;
/*!40000 ALTER TABLE `servers` DISABLE KEYS */;
INSERT INTO `servers` VALUES (1,'jiva'),(2,'test');
/*!40000 ALTER TABLE `servers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'login'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-04-22  2:41:36
