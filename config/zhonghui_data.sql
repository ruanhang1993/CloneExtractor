-- MySQL dump 10.13  Distrib 5.7.14, for Win64 (x86_64)
--
-- Host: localhost    Database: zhonghui_data
-- ------------------------------------------------------
-- Server version	5.7.14-log

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
-- Table structure for table `author`
--

DROP TABLE IF EXISTS `author`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `author` (
  `author_id` int(32) NOT NULL AUTO_INCREMENT,
  `author_nick` varchar(255) DEFAULT NULL,
  `author_name` varchar(255) DEFAULT NULL,
  `commit_id` int(32) DEFAULT NULL,
  PRIMARY KEY (`author_id`)
) ENGINE=InnoDB AUTO_INCREMENT=60 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cc_stream_property`
--

DROP TABLE IF EXISTS `cc_stream_property`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cc_stream_property` (
  `stream` varchar(255) NOT NULL,
  `blstream` varchar(255) NOT NULL,
  `view` varchar(255) NOT NULL,
  `view_local_path` varchar(255) NOT NULL,
  PRIMARY KEY (`stream`),
  UNIQUE KEY `stream_UNIQUE` (`stream`),
  UNIQUE KEY `blstream_UNIQUE` (`blstream`),
  UNIQUE KEY `view_UNIQUE` (`view`),
  UNIQUE KEY `view_local_path_UNIQUE` (`view_local_path`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `change`
--

DROP TABLE IF EXISTS `change`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `change` (
  `change_id` int(32) NOT NULL AUTO_INCREMENT,
  `change_type` int(32) DEFAULT NULL,
  `begin_line` int(32) DEFAULT NULL,
  `end_line` int(32) DEFAULT NULL,
  `change_fragment` text,
  `commit_id` int(32) DEFAULT NULL,
  `pre_commit_id` int(32) DEFAULT NULL,
  `file_id` int(32) DEFAULT NULL,
  `old_file_id` int(32) DEFAULT NULL,
  `compareId` int(32) DEFAULT NULL,
  `echloc` int(32) DEFAULT NULL,
  `chbloc` int(32) DEFAULT NULL,
  `chcmloc` int(32) DEFAULT NULL,
  PRIMARY KEY (`change_id`),
  KEY `change_fk1` (`commit_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `change_file`
--

DROP TABLE IF EXISTS `change_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `change_file` (
  `change_file_id` int(32) NOT NULL AUTO_INCREMENT,
  `file_name` varchar(255) DEFAULT NULL,
  `commit_id` int(32) DEFAULT NULL,
  `file_id` int(32) DEFAULT NULL,
  `compare_id` int(32) DEFAULT NULL,
  `loc` int(32) DEFAULT NULL,
  `eloc` int(32) DEFAULT NULL,
  `cloc` int(32) DEFAULT NULL,
  `ecloc` int(32) DEFAULT NULL,
  `chloc` int(32) DEFAULT NULL,
  `echloc` int(32) DEFAULT NULL,
  `dcloc` int(32) DEFAULT NULL,
  `edcloc` int(32) DEFAULT NULL,
  `content` longblob,
  `bdcloc` int(32) DEFAULT NULL,
  `bchloc` int(32) DEFAULT NULL,
  `bcloc` int(32) DEFAULT NULL,
  `bloc` int(32) DEFAULT NULL,
  `cmdcloc` int(32) DEFAULT NULL,
  `cmchloc` int(32) DEFAULT NULL,
  `cmcloc` int(32) DEFAULT NULL,
  `cmloc` int(32) DEFAULT NULL,
  `ccloc` int(32) DEFAULT NULL,
  `eccloc` int(32) DEFAULT NULL,
  `file_type` varchar(45) DEFAULT NULL,
  `aloc` int(32) DEFAULT NULL,
  `ealoc` int(32) DEFAULT NULL,
  `dloc` int(32) DEFAULT NULL,
  `edloc` int(32) DEFAULT NULL,
  PRIMARY KEY (`change_file_id`),
  KEY `file_name` (`file_name`),
  KEY `file_name_2` (`file_name`),
  KEY `file_fk1` (`commit_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `clone`
--

DROP TABLE IF EXISTS `clone`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `clone` (
  `clone_id` int(32) NOT NULL AUTO_INCREMENT,
  `begin_line` int(255) DEFAULT NULL,
  `end_line` int(32) DEFAULT NULL,
  `ecloc` int(32) DEFAULT NULL,
  `clone_fragment` text,
  `clone_size` int(32) DEFAULT NULL,
  `clone_type` int(32) DEFAULT NULL,
  `file_id` int(32) DEFAULT NULL,
  `clone_class_id` int(32) DEFAULT NULL,
  `commit_id` int(32) DEFAULT NULL,
  `bcloc` int(32) DEFAULT NULL,
  `cmcloc` int(32) DEFAULT NULL,
  `ccloc` int(32) DEFAULT NULL,
  `eccloc` int(32) DEFAULT NULL,
  `repeat_time` int(32) DEFAULT NULL,
  PRIMARY KEY (`clone_id`),
  KEY `clone_fk1` (`clone_class_id`),
  KEY `clone_fk2` (`commit_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10165 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `clone_class`
--

DROP TABLE IF EXISTS `clone_class`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `clone_class` (
  `clone_class_id` int(32) NOT NULL AUTO_INCREMENT,
  `commit_id` int(32) DEFAULT NULL,
  `type` int(32) DEFAULT NULL,
  PRIMARY KEY (`clone_class_id`),
  KEY `clone_class_fk1` (`commit_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2568 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `clone_in_change`
--

DROP TABLE IF EXISTS `clone_in_change`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `clone_in_change` (
  `clone_in_change_id` int(32) NOT NULL AUTO_INCREMENT,
  `change_id` int(32) DEFAULT NULL,
  `clone_id` int(32) DEFAULT NULL,
  `file_id` int(32) DEFAULT NULL,
  `clone_class_id` int(32) DEFAULT NULL,
  `compare_id` int(32) DEFAULT NULL,
  `clone_type` int(32) DEFAULT NULL,
  `change_type` int(32) DEFAULT NULL,
  `ecloc` int(32) DEFAULT NULL,
  `begin_line` int(32) DEFAULT NULL,
  `end_line` int(32) DEFAULT NULL,
  `bcloc` int(32) DEFAULT NULL,
  `cmcloc` int(32) DEFAULT NULL,
  `ccloc` int(32) DEFAULT NULL,
  `eccloc` int(32) DEFAULT NULL,
  PRIMARY KEY (`clone_in_change_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `commit`
--

DROP TABLE IF EXISTS `commit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `commit` (
  `commit_id` int(32) NOT NULL AUTO_INCREMENT,
  `stage` int(11) DEFAULT '0',
  `revision_id` varchar(255) DEFAULT NULL,
  `pre_revision_id` varchar(255) DEFAULT NULL,
  `commit_date` datetime DEFAULT NULL,
  `commit_log` text,
  `author_id` int(32) DEFAULT NULL,
  `project_id` int(32) DEFAULT NULL,
  `file_num` int(32) DEFAULT NULL,
  `clone_file_num` int(32) DEFAULT NULL,
  `loc` int(32) DEFAULT NULL,
  `eloc` int(32) DEFAULT NULL,
  `cloc` int(32) DEFAULT NULL,
  `ecloc` int(32) DEFAULT NULL,
  `bloc` int(32) DEFAULT NULL,
  `cmloc` int(32) DEFAULT NULL,
  `bcloc` int(32) DEFAULT NULL,
  `cmcloc` int(32) DEFAULT NULL,
  `ccloc` int(32) DEFAULT NULL,
  `eccloc` int(32) DEFAULT NULL,
  `commit_code` varchar(255) DEFAULT NULL,
  `self_version_id` varchar(255) DEFAULT NULL,
  `submit_date` datetime DEFAULT NULL,
  `submit_done_date` datetime DEFAULT NULL,
  `scan_date` datetime DEFAULT NULL,
  PRIMARY KEY (`commit_id`),
  KEY `commit_fk1` (`author_id`),
  KEY `commit_fk2` (`project_id`)
) ENGINE=InnoDB AUTO_INCREMENT=117 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `commit_language`
--

DROP TABLE IF EXISTS `commit_language`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `commit_language` (
  `id` int(32) NOT NULL AUTO_INCREMENT,
  `commit_id` int(32) NOT NULL,
  `file_num` int(32) DEFAULT NULL,
  `clone_file_num` int(32) DEFAULT NULL,
  `loc` int(32) DEFAULT NULL,
  `eloc` int(32) DEFAULT NULL,
  `cloc` int(32) DEFAULT NULL,
  `ecloc` int(32) DEFAULT NULL,
  `language` varchar(45) NOT NULL,
  `bloc` int(32) DEFAULT NULL,
  `cmloc` int(32) DEFAULT NULL,
  `bcloc` int(32) DEFAULT NULL,
  `cmcloc` int(32) DEFAULT NULL,
  `ccloc` int(32) DEFAULT NULL,
  `eccloc` int(32) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=197 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `compare`
--

DROP TABLE IF EXISTS `compare`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `compare` (
  `compare_id` int(32) NOT NULL AUTO_INCREMENT,
  `change_id` int(32) DEFAULT NULL,
  `project_id` int(32) DEFAULT NULL,
  `commit_id` int(32) NOT NULL,
  `pre_commit_id` int(32) DEFAULT NULL,
  `clone_in_change_id` int(32) DEFAULT NULL,
  `revision_id` varchar(255) DEFAULT NULL,
  `pre_revision_id` varchar(255) DEFAULT NULL,
  `compare_date` datetime DEFAULT NULL,
  `time_span` varchar(255) DEFAULT '0',
  `version_span` int(32) DEFAULT NULL,
  `change_file_num` int(32) DEFAULT NULL,
  `clone_change_file_num` int(32) DEFAULT NULL,
  `loc` int(32) DEFAULT NULL,
  `file_num` int(32) DEFAULT NULL,
  `clone_file_num` int(32) DEFAULT NULL,
  `eloc` int(32) DEFAULT NULL,
  `cloc` int(32) DEFAULT NULL,
  `ecloc` int(32) DEFAULT NULL,
  `dcloc` int(32) DEFAULT NULL,
  `edcloc` int(32) DEFAULT NULL,
  `chloc` int(32) DEFAULT NULL,
  `echloc` int(32) DEFAULT NULL,
  `status` int(32) DEFAULT NULL,
  `bloc` int(32) DEFAULT NULL,
  `cmloc` int(32) DEFAULT NULL,
  `bcloc` int(32) DEFAULT NULL,
  `cmcloc` int(32) DEFAULT NULL,
  `bdcloc` int(32) DEFAULT NULL,
  `cmdcloc` int(32) DEFAULT NULL,
  `bchloc` int(32) DEFAULT NULL,
  `cmchloc` int(32) DEFAULT NULL,
  `ccloc` int(32) DEFAULT NULL,
  `eccloc` int(32) DEFAULT NULL,
  `aloc` int(32) DEFAULT NULL,
  `ealoc` int(32) DEFAULT NULL,
  `dloc` int(32) DEFAULT NULL,
  `edloc` int(32) DEFAULT NULL,
  `process_date` datetime DEFAULT NULL,
  `process_done_date` datetime DEFAULT NULL,
  PRIMARY KEY (`compare_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `compare_language`
--

DROP TABLE IF EXISTS `compare_language`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `compare_language` (
  `id` int(32) NOT NULL AUTO_INCREMENT,
  `compare_id` int(32) NOT NULL,
  `language` varchar(45) NOT NULL,
  `revision_id` varchar(255) DEFAULT NULL,
  `pre_revision_id` varchar(255) DEFAULT NULL,
  `commit_id` int(32) DEFAULT NULL,
  `pre_commit_id` int(32) DEFAULT NULL,
  `change_file_num` int(32) DEFAULT NULL,
  `clone_change_file_num` int(32) DEFAULT NULL,
  `loc` int(32) DEFAULT NULL,
  `eloc` int(32) DEFAULT NULL,
  `cloc` int(32) DEFAULT NULL,
  `ecloc` int(32) DEFAULT NULL,
  `chloc` int(32) DEFAULT NULL,
  `echloc` int(32) DEFAULT NULL,
  `aloc` int(32) DEFAULT NULL,
  `ealoc` int(32) DEFAULT NULL,
  `dloc` int(32) DEFAULT NULL,
  `dcloc` int(32) DEFAULT NULL,
  `edcloc` int(32) DEFAULT NULL,
  `bloc` int(32) DEFAULT NULL,
  `cmloc` int(32) DEFAULT NULL,
  `bcloc` int(32) DEFAULT NULL,
  `cmcloc` int(32) DEFAULT NULL,
  `bdcloc` int(32) DEFAULT NULL,
  `cmdcloc` int(32) DEFAULT NULL,
  `bchloc` int(32) DEFAULT NULL,
  `cmchloc` int(32) DEFAULT NULL,
  `ccloc` int(32) DEFAULT NULL,
  `eccloc` int(32) DEFAULT NULL,
  `edloc` int(32) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `diff`
--

DROP TABLE IF EXISTS `diff`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `diff` (
  `change_id` int(32) NOT NULL AUTO_INCREMENT,
  `change_type` int(32) DEFAULT NULL,
  `begin_line` int(32) DEFAULT NULL,
  `end_line` int(32) DEFAULT NULL,
  `change_fragment` text,
  `commit_id` int(32) DEFAULT NULL,
  `compare_id` int(32) DEFAULT NULL,
  `pre_commit_id` int(32) DEFAULT NULL,
  `file_id` int(32) DEFAULT NULL,
  `echloc` int(32) DEFAULT NULL,
  `old_file_id` int(32) DEFAULT NULL,
  `bchloc` int(32) DEFAULT NULL,
  `cmchloc` int(32) DEFAULT NULL,
  PRIMARY KEY (`change_id`),
  KEY `change_fk1` (`commit_id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `file`
--

DROP TABLE IF EXISTS `file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `file` (
  `file_id` int(32) NOT NULL AUTO_INCREMENT,
  `file_name` varchar(255) DEFAULT NULL,
  `commit_id` int(32) DEFAULT NULL,
  `loc` int(32) DEFAULT NULL,
  `eloc` int(32) DEFAULT NULL,
  `cloc` int(32) DEFAULT NULL,
  `ecloc` int(32) DEFAULT NULL,
  `content` longblob,
  `valid_file_name` varchar(255) DEFAULT NULL,
  `bloc` int(32) DEFAULT NULL,
  `cmloc` int(32) DEFAULT NULL,
  `bcloc` int(32) DEFAULT NULL,
  `cmcloc` int(32) DEFAULT NULL,
  `ccloc` int(32) DEFAULT NULL,
  `eccloc` int(32) DEFAULT NULL,
  `clone_num` int(32) DEFAULT NULL,
  `clone_class_num` int(32) DEFAULT NULL,
  `file_type` varchar(45) DEFAULT NULL,
  `rename_tag` int(11) DEFAULT '0',
  PRIMARY KEY (`file_id`),
  KEY `file_name` (`file_name`),
  KEY `file_name_2` (`file_name`),
  KEY `file_fk1` (`commit_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13850 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `program_language`
--

DROP TABLE IF EXISTS `program_language`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `program_language` (
  `language` varchar(255) NOT NULL,
  `suffix` text NOT NULL,
  `ignored` text,
  PRIMARY KEY (`language`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `project`
--

DROP TABLE IF EXISTS `project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project` (
  `project_id` int(32) NOT NULL AUTO_INCREMENT,
  `project_name_ch` varchar(255) DEFAULT NULL,
  `project_name_en` varchar(255) DEFAULT NULL,
  `develop_company` varchar(255) DEFAULT NULL,
  `project_path` varchar(255) DEFAULT NULL,
  `project_language` varchar(255) DEFAULT NULL,
  `project_team` varchar(255) DEFAULT NULL,
  `repository_id` int(32) DEFAULT NULL,
  `commit_id` int(32) DEFAULT NULL,
  `stream` text,
  `pvob` text,
  `component` text,
  `view` text,
  `view_local_path` text,
  PRIMARY KEY (`project_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `repository`
--

DROP TABLE IF EXISTS `repository`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `repository` (
  `repository_id` int(32) NOT NULL AUTO_INCREMENT,
  `url` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `language` varchar(255) DEFAULT NULL,
  `process_date` datetime DEFAULT NULL,
  `analyze_type` int(11) DEFAULT '0',
  `commit_date` datetime DEFAULT NULL,
  PRIMARY KEY (`repository_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `team`
--

DROP TABLE IF EXISTS `team`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `team` (
  `team_id` int(32) NOT NULL AUTO_INCREMENT,
  `team_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`team_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-12-07 16:04:04
