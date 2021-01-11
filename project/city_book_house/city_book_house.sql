/*
 Navicat Premium Data Transfer

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 80012
 Source Host           : 127.0.0.1:3306
 Source Schema         : city_book_house

 Target Server Type    : MySQL
 Target Server Version : 80012
 File Encoding         : 65001

 Date: 11/01/2021 19:01:21
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for cb_books
-- ----------------------------
DROP TABLE IF EXISTS `cb_books`;
CREATE TABLE `cb_books`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `book_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '书名',
  `author` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '作者',
  `first_category_id` int(11) NOT NULL DEFAULT 0 COMMENT '一级分类ID',
  `second_category_id` int(11) NOT NULL DEFAULT 0 COMMENT '二级分类ID',
  `stock` int(11) NOT NULL DEFAULT 0 COMMENT '库存/馆藏量',
  `book_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '编码',
  `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '1正常 2下架',
  `place` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '位置',
  `introduction` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '简介',
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '图片',
  `create_time` timestamp(0) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `first_category_id`(`first_category_id`) USING BTREE,
  INDEX `second_category_id`(`second_category_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for cb_borrow_record
-- ----------------------------
DROP TABLE IF EXISTS `cb_borrow_record`;
CREATE TABLE `cb_borrow_record`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `book_id` int(11) NULL DEFAULT NULL,
  `status` tinyint(4) NULL DEFAULT NULL COMMENT '1借阅 2续借 3超时 4归还',
  `start_time` timestamp(0) NULL DEFAULT NULL COMMENT '起借时间',
  `end_time` timestamp(0) NULL DEFAULT NULL COMMENT '归还时间',
  `create_time` timestamp(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '借阅记录表' ROW_FORMAT = Fixed;

-- ----------------------------
-- Records of cb_borrow_record
-- ----------------------------
INSERT INTO `cb_borrow_record` VALUES (1, 1, 1, 1, '2021-01-07 17:13:22', '2021-01-07 17:13:24', '2021-01-07 17:13:27');
INSERT INTO `cb_borrow_record` VALUES (2, 2, 2, 2, '2021-01-07 17:13:32', '2021-01-07 17:13:35', '2021-01-07 17:13:39');

-- ----------------------------
-- Table structure for cb_category
-- ----------------------------
DROP TABLE IF EXISTS `cb_category`;
CREATE TABLE `cb_category`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `category_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
  `pid` int(11) NOT NULL DEFAULT 0 COMMENT '0为一级分类',
  `create_time` timestamp(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cb_category
-- ----------------------------
INSERT INTO `cb_category` VALUES (1, 'IT', 0, '2021-01-08 16:42:40');
INSERT INTO `cb_category` VALUES (2, '美食', 0, '2021-01-08 16:42:51');
INSERT INTO `cb_category` VALUES (3, 'Java', 1, '2021-01-08 16:43:09');
INSERT INTO `cb_category` VALUES (4, 'HTML', 1, '2021-01-08 16:43:24');
INSERT INTO `cb_category` VALUES (5, 'CSS', 1, '2021-01-08 16:43:33');
INSERT INTO `cb_category` VALUES (6, 'MySQL', 1, '2021-01-08 16:43:49');
INSERT INTO `cb_category` VALUES (7, '粤菜', 2, '2021-01-08 16:44:04');
INSERT INTO `cb_category` VALUES (8, '川菜', 2, '2021-01-08 16:44:11');
INSERT INTO `cb_category` VALUES (9, '湘菜', 2, '2021-01-08 16:44:18');
INSERT INTO `cb_category` VALUES (10, '美食', 0, '2021-01-11 11:06:05');
INSERT INTO `cb_category` VALUES (11, 'test2', 0, '2021-01-11 13:19:51');
INSERT INTO `cb_category` VALUES (12, '测试', 0, '2021-01-11 13:31:22');
INSERT INTO `cb_category` VALUES (13, '人工智能', 0, '2021-01-11 13:31:22');
INSERT INTO `cb_category` VALUES (14, '测试3', 12, '2021-01-11 13:32:13');

-- ----------------------------
-- Table structure for cb_manager
-- ----------------------------
DROP TABLE IF EXISTS `cb_manager`;
CREATE TABLE `cb_manager`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `account` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `gender` tinyint(4) NOT NULL COMMENT '1男 2女 3未知',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '1正常 2禁用',
  `has_rights` tinyint(4) NULL DEFAULT NULL COMMENT '1超管 2普管',
  `create_time` timestamp(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户申请表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cb_manager
-- ----------------------------
INSERT INTO `cb_manager` VALUES (1, 'admin', '123456', 'admin', 1, '13515013510', NULL, 1, 1, '2021-01-04 11:49:10');
INSERT INTO `cb_manager` VALUES (10, 'adminadmin', NULL, '小红', 2, '13515013510', 'adawadad', 0, 0, '2021-01-06 15:12:15');
INSERT INTO `cb_manager` VALUES (5, 'manager888', 'asd123asd', 'manager', 0, '18819917710', 'test9572', 1, 1, '2021-01-05 12:10:59');
INSERT INTO `cb_manager` VALUES (6, 'manager888', 'asd123asd', 'manager', 0, '18819917710', 'test9572', 1, 1, '2021-01-05 12:10:59');
INSERT INTO `cb_manager` VALUES (7, 'manager888', 'asd123asd', 'manager', 0, '18819917710', 'test9572', 1, 1, '2021-01-05 12:10:59');
INSERT INTO `cb_manager` VALUES (8, 'manager888', 'asd123asd', 'manager', 0, '18819917710', 'test9572', 1, 1, '2021-01-05 12:10:59');
INSERT INTO `cb_manager` VALUES (9, 'ironman666', '123456', 'ironman', 1, '13515013510', '1234567890', 1, 2, '2021-01-06 12:06:06');
INSERT INTO `cb_manager` VALUES (12, 'kobe9527', 'e10adc3949ba59abbe56e057f20f883e', 'kobe', 1, '13314415510', NULL, 0, 0, '2021-01-11 12:00:40');
INSERT INTO `cb_manager` VALUES (11, 'kobe9527', 'e10adc3949ba59abbe56e057f20f883e', 'kobe', 1, '13314415510', 'adawadad', 0, 0, '2021-01-06 15:18:41');

-- ----------------------------
-- Table structure for cb_user_apply
-- ----------------------------
DROP TABLE IF EXISTS `cb_user_apply`;
CREATE TABLE `cb_user_apply`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `id_card` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '身份证号',
  `id_card_positive_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '身份证正面照',
  `id_card_negative_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '身份证反面照',
  `status` tinyint(4) NULL DEFAULT NULL COMMENT '1审核通过 -1不通过 2待审',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` timestamp(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
