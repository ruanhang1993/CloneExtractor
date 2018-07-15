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
-- Dumping data for table `cc_stream_property`
--

LOCK TABLES `cc_stream_property` WRITE;
/*!40000 ALTER TABLE `cc_stream_property` DISABLE KEYS */;
INSERT INTO `cc_stream_property` VALUES ('NGCNYS_Front_ST@\\NGCNYTS_PVOB','NGCNYS_Frontend_Clone@\\NGCNYTS_PVOB','codescanner_NGCNYS_Frontend_Clone','D:\\TestCC\\NGNYTS_Fronted_Clone');
/*!40000 ALTER TABLE `cc_stream_property` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-11-18  9:55:58
