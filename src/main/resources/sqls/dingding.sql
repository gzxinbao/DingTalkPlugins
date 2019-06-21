/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50711
Source Host           : localhost:3306
Source Database       : dingding

Target Server Type    : MYSQL
Target Server Version : 50711
File Encoding         : 65001

Date: 2019-06-20 09:56:28
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for dd_component_annual_leave
-- ----------------------------
DROP TABLE IF EXISTS `dd_component_annual_leave`;
CREATE TABLE `dd_component_annual_leave` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `confirm_join_time` date DEFAULT NULL,
  `is_admin` bit(1) DEFAULT NULL,
  `join_working_time` date DEFAULT NULL,
  `user_id` varchar(200) DEFAULT NULL,
  `user_name` varchar(32) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  `regular_time` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for dd_component_annual_leave_flow
-- ----------------------------
DROP TABLE IF EXISTS `dd_component_annual_leave_flow`;
CREATE TABLE `dd_component_annual_leave_flow` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `pass_days` float DEFAULT NULL,
  `pass_days_last` float DEFAULT NULL,
  `total_days` float DEFAULT NULL,
  `user_id` varchar(200) DEFAULT NULL,
  `year` date DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=87 DEFAULT CHARSET=utf8mb4;


SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for dd_component_annual_leave_log
-- ----------------------------
DROP TABLE IF EXISTS `dd_component_annual_leave_log`;
CREATE TABLE `dd_component_annual_leave_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` varchar(200) DEFAULT NULL,
  `check_type` varchar(255) DEFAULT NULL,
  `duration_in_day` float DEFAULT NULL,
  `days_last_year` float DEFAULT NULL,
  `days_this_year` float DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  `title` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for dd_component_annual_leave_message
-- ----------------------------
DROP TABLE IF EXISTS `dd_component_annual_leave_message`;
CREATE TABLE `dd_component_annual_leave_message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_date` datetime DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `check_message` varchar(255) DEFAULT NULL,
  `content` varchar(255) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;