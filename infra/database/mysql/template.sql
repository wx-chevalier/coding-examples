-- ----------------------------
--  root/root@mysql
-- ----------------------------
-- 创建 test 数据库
DROP DATABASE IF EXISTS cs_test;
CREATE DATABASE cs_test;

-- 配置使用 Test 数据库
use cs_test;
SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `_id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '_ID，内部自增编号',
  `uuid` varchar(40) DEFAULT NULL,
  `name` varchar(32) DEFAULT NULL,
  `password` varchar(64) DEFAULT NULL,
  `salt` varchar(64) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `deleted_at` datetime DEFAULT NULL,
  PRIMARY KEY (`_id`),
  KEY `uuid` (`uuid`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Table structure for `product`
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `_id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '_ID，内部自增编号',
  `code` varchar(6) DEFAULT NULL,
  `name` varchar(15) DEFAULT NULL,
  `price` DECIMAL(4,2) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `deleted_at` datetime DEFAULT NULL,
  PRIMARY KEY (`_id`),
  KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;
