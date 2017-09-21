/*
Navicat MySQL Data Transfer

Source Server         : 127.0.0.1
Source Server Version : 50617
Source Host           : localhost:3306
Source Database       : sledusql

Target Server Type    : MYSQL
Target Server Version : 50617
File Encoding         : 65001

Date: 2017-04-07 16:50:00
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `course`
-- ----------------------------
DROP TABLE IF EXISTS `course`;
CREATE TABLE `course` (
  `cno` varchar(10) NOT NULL,
  `cname` varchar(10) NOT NULL,
  `tno` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of course
-- ----------------------------
INSERT INTO `course` VALUES ('3-105', '计算机导论', '825');
INSERT INTO `course` VALUES ('3-245', '操作系统', '804');
INSERT INTO `course` VALUES ('6-166', '数字电路', '856');
INSERT INTO `course` VALUES ('9-888', '高等数学', '831');

-- ----------------------------
-- Table structure for `score`
-- ----------------------------
DROP TABLE IF EXISTS `score`;
CREATE TABLE `score` (
  `sno` varchar(5) NOT NULL,
  `cno` varchar(10) NOT NULL,
  `degree` decimal(4,1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of score
-- ----------------------------
INSERT INTO `score` VALUES ('103', '3-245', '86.0');
INSERT INTO `score` VALUES ('105', '3-245', '75.0');
INSERT INTO `score` VALUES ('109', '3-245', '68.0');
INSERT INTO `score` VALUES ('103', '3-105', '92.0');
INSERT INTO `score` VALUES ('105', '3-105', '88.0');
INSERT INTO `score` VALUES ('109', '3-105', '76.0');
INSERT INTO `score` VALUES ('101', '3-105', '64.0');
INSERT INTO `score` VALUES ('107', '3-105', '91.0');
INSERT INTO `score` VALUES ('108', '3-105', '78.0');
INSERT INTO `score` VALUES ('101', '6-166', '85.0');
INSERT INTO `score` VALUES ('107', '6-166', '79.0');
INSERT INTO `score` VALUES ('108', '6-166', '81.0');

-- ----------------------------
-- Table structure for `students`
-- ----------------------------
DROP TABLE IF EXISTS `students`;
CREATE TABLE `students` (
  `sno` varchar(3) NOT NULL,
  `sname` varchar(10) NOT NULL,
  `ssex` varchar(10) NOT NULL,
  `birthday` datetime DEFAULT NULL,
  `class` varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of students
-- ----------------------------
INSERT INTO `students` VALUES ('108', '曾华', '男', '1998-09-01 00:00:00', '95033');
INSERT INTO `students` VALUES ('105', '匡明', '男', '1996-10-02 00:00:00', '95031');
INSERT INTO `students` VALUES ('107', '王丽', '女', '1998-01-23 00:00:00', '95033');
INSERT INTO `students` VALUES ('101', '李军', '男', '1998-06-20 00:00:00', '95033');
INSERT INTO `students` VALUES ('109', '王芳', '女', '1997-02-20 00:00:00', '95031');
INSERT INTO `students` VALUES ('103', '陆君', '男', '1997-06-03 00:00:00', '95031');

-- ----------------------------
-- Table structure for `teacher`
-- ----------------------------
DROP TABLE IF EXISTS `teacher`;
CREATE TABLE `teacher` (
  `tno` varchar(5) NOT NULL,
  `tname` varchar(10) NOT NULL,
  `tsex` varchar(5) NOT NULL,
  `tbirthday` datetime DEFAULT NULL,
  `prof` varchar(20) DEFAULT NULL,
  `depart` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of teacher
-- ----------------------------
INSERT INTO `teacher` VALUES ('804', '李诚', '男', '1958-12-02 00:00:00', '副教授', '计算机系');
INSERT INTO `teacher` VALUES ('856', '张旭', '男', '1969-03-12 00:00:00', '讲师', '电子工程系');
INSERT INTO `teacher` VALUES ('825', '王萍', '女', '1972-05-05 00:00:00', '助教', '计算机系');
INSERT INTO `teacher` VALUES ('831', '刘冰', '女', '1977-08-14 00:00:00', '助教', '电子工程系');
