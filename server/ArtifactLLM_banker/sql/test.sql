/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 90400 (9.4.0)
 Source Host           : localhost:3306
 Source Schema         : artifact_llm

 Target Server Type    : MySQL
 Target Server Version : 90400 (9.4.0)
 File Encoding         : 65001

 Date: 16/12/2025 18:59:41
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ai_chat_history
-- ----------------------------
DROP TABLE IF EXISTS `ai_chat_history`;
CREATE TABLE `ai_chat_history`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `session_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '会话ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `user_input` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '用户提问',
  `ai_response` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'AI回答',
  `model` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '使用模型',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '对话时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_session`(`session_id` ASC) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'AI对话历史表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ai_chat_history
-- ----------------------------
INSERT INTO `ai_chat_history` VALUES (1, 'demo-session-1', 4, '帮我概括一下后母戊鼎的历史价值。', '后母戊鼎是商代晚期最大最重的青铜礼器之一，体现了商王朝高度发达的青铜铸造技术与礼制文化，是研究商代政治、宗教与铸造工艺的重要实物。', 'gpt-4.1-mini', '2025-12-16 10:27:30');
INSERT INTO `ai_chat_history` VALUES (2, 'demo-session-1', 4, '和四羊方尊相比，它们有什么共同点？', '两者同属商代晚期重要青铜礼器，都体现高超铸造工艺与复杂纹饰，用于宗教祭祀场合，在造型和艺术风格上具有浓厚的商代特色。', 'gpt-4.1-mini', '2025-12-16 10:27:30');
INSERT INTO `ai_chat_history` VALUES (3, 'demo-session-2', 5, '适合作为课堂案例的唐代文物有哪些？', '可以选择唐三彩骆驼俑展示丝绸之路贸易与丧葬习俗，也可以结合唐代金银器、壁画等文物，从多角度介绍唐代开放多元的文化面貌。', 'gpt-4.1-mini', '2025-12-16 10:27:30');

-- ----------------------------
-- Table structure for audit
-- ----------------------------
DROP TABLE IF EXISTS `audit`;
CREATE TABLE `audit`  (
  `audit_id` int NOT NULL AUTO_INCREMENT COMMENT '审核ID',
  `audit_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'pending' COMMENT '状态(pending/approved/rejected)',
  `audit_type_id` int NOT NULL COMMENT '审核类型ID',
  `audit_data` json NOT NULL COMMENT '待审核数据快照(JSON)',
  `created_by` bigint NOT NULL COMMENT '申请人ID',
  `auditor_id` bigint NULL DEFAULT NULL COMMENT '审核人ID',
  `audit_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `reject_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '驳回原因',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`audit_id`) USING BTREE,
  INDEX `idx_creator`(`created_by` ASC) USING BTREE,
  INDEX `idx_status`(`audit_status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '审核记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of audit
-- ----------------------------
INSERT INTO `audit` VALUES (1, 'approved', 1, '{\"action\": \"create\", \"relics_id\": 21, \"relics_name\": \"演示新增文物\"}', 2, 1, '2025-01-10 10:00:00', NULL, '2025-12-16 10:27:30', '2025-12-16 10:27:30');
INSERT INTO `audit` VALUES (2, 'rejected', 2, '{\"field\": \"description\", \"action\": \"update\", \"relics_id\": 5}', 3, 1, '2025-01-11 15:30:00', '描述内容与史实不符', '2025-12-16 10:27:30', '2025-12-16 10:27:30');
INSERT INTO `audit` VALUES (3, 'pending', 2, '{\"field\": \"current_location\", \"action\": \"update\", \"relics_id\": 3}', 2, NULL, NULL, NULL, '2025-12-16 10:27:30', '2025-12-16 10:27:30');

-- ----------------------------
-- Table structure for audit_type
-- ----------------------------
DROP TABLE IF EXISTS `audit_type`;
CREATE TABLE `audit_type`  (
  `audit_type_id` int NOT NULL AUTO_INCREMENT COMMENT '审核类型ID',
  `audit_type_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '类型名称(新增/修改/删除)',
  `audit_content_fields` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '审核内容描述',
  PRIMARY KEY (`audit_type_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '审核类型表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of audit_type
-- ----------------------------
INSERT INTO `audit_type` VALUES (1, '新增文物', 'relics_name, era, material, relics_type_id');
INSERT INTO `audit_type` VALUES (2, '修改文物', 'relics_id, modified_fields');
INSERT INTO `audit_type` VALUES (3, '删除文物', 'relics_id, reason');

-- ----------------------------
-- Table structure for cultural_relics
-- ----------------------------
DROP TABLE IF EXISTS `cultural_relics`;
CREATE TABLE `cultural_relics`  (
  `relics_id` int NOT NULL AUTO_INCREMENT COMMENT '文物ID',
  `relics_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文物名称',
  `era` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '年代',
  `material` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '材质',
  `relics_type_id` int NOT NULL COMMENT '类型ID',
  `discovery_site` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '出土地点',
  `current_location` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '现藏地',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文物描述',
  `custom_fields` json NULL COMMENT '动态字段值(JSON)',
  `created_by` bigint NOT NULL COMMENT '创建者ID',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`relics_id`) USING BTREE,
  INDEX `idx_creator`(`created_by` ASC) USING BTREE,
  INDEX `idx_type`(`relics_type_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文物表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cultural_relics
-- ----------------------------
INSERT INTO `cultural_relics` VALUES (1, '后母戊鼎', '商周', '青铜', 1, '河南省安阳市小屯村殷墟', '国家博物馆', '商代晚期青铜礼器中的代表作，体量巨大，工艺精湛。', '{\"重量\": \"832.84kg\", \"高度\": \"133cm\"}', 2, '2025-12-16 10:27:30', '2025-12-16 17:37:37', 0);
INSERT INTO `cultural_relics` VALUES (2, '四羊方尊', '商周', '青铜', 1, '湖南省宁乡县月山铺', '国家博物馆', '造型复杂、纹饰华丽的方尊，四角立羊，结构巧妙。', '{\"重量\": \"34.5kg\", \"高度\": \"58cm\"}', 2, '2025-12-16 10:27:30', '2025-12-16 17:37:40', 0);
INSERT INTO `cultural_relics` VALUES (3, '曾侯乙编钟', '商周', '青铜', 1, '湖北省随州市擂鼓墩曾侯乙墓', '湖北省博物馆', '大型编钟乐器组合，代表先秦礼乐文明的高峰。', '{\"重量\": \"2500kg\", \"高度\": \"273cm\"}', 3, '2025-12-16 10:27:30', '2025-12-16 17:37:59', 0);
INSERT INTO `cultural_relics` VALUES (4, '秦始皇兵马俑步兵俑', '秦汉', '陶瓷', 2, '陕西省西安市临潼区骊山镇', '秦始皇帝陵博物院', '姿态各异的兵马俑之一，展现秦军军容。', '{\"窑口\": \"秦始皇陵陪葬坑\", \"釉色\": \"原为彩绘，现多脱落\"}', 2, '2025-12-16 10:27:30', '2025-12-16 17:41:18', 0);
INSERT INTO `cultural_relics` VALUES (5, '唐三彩骆驼俑', '隋唐', '陶瓷', 2, '河南省洛阳市', '洛阳博物馆', '唐代三彩陶俑代表作品之一，造型生动，色彩绚丽。', '{\"窑口\": \"洛阳窑\", \"釉色\": \"黄绿白三彩\"}', 2, '2025-12-16 10:27:30', '2025-12-16 17:41:23', 0);
INSERT INTO `cultural_relics` VALUES (6, '汝窑天青釉洗', '宋元', '陶瓷', 2, '河南省宝丰清凉寺汝官窑遗址', '台北故宫博物院', '传世稀少的汝窑器物，釉色温润如玉。', '{\"窑口\": \"汝窑\", \"釉色\": \"天青色\"}', 3, '2025-12-16 10:27:30', '2025-12-16 17:41:27', 0);
INSERT INTO `cultural_relics` VALUES (7, '官窑青釉葵口碗', '宋元', '陶瓷', 2, '浙江省杭州市', '浙江省博物馆', '南宋官窑代表器形之一，釉色粉青，开片细密。', '{\"窑口\": \"南宋官窑\", \"釉色\": \"粉青釉\"}', 3, '2025-12-16 10:27:30', '2025-12-16 17:41:31', 0);
INSERT INTO `cultural_relics` VALUES (8, '元青花萧何月下追韩信图梅瓶', '宋元', '陶瓷', 2, '江西省景德镇', '上海博物馆', '经典青花故事纹器物，绘画生动，构图饱满。', '{\"窑口\": \"景德镇窑\", \"釉色\": \"白釉蓝花\"}', 3, '2025-12-16 10:27:30', '2025-12-16 17:41:35', 0);
INSERT INTO `cultural_relics` VALUES (9, '明成化斗彩鸡缸杯', '明清', '陶瓷', 2, '江西省景德镇', '台北故宫博物院', '成化斗彩名品之一，胎釉精细，纹饰清新。', '{\"窑口\": \"景德镇御窑厂\", \"釉色\": \"斗彩\"}', 3, '2025-12-16 10:27:30', '2025-12-16 17:41:38', 0);
INSERT INTO `cultural_relics` VALUES (10, '清乾隆粉彩九桃天球瓶', '明清', '陶瓷', 2, '江西省景德镇', '故宫博物院', '粉彩瓷器代表作品，寓意多福多寿。', '{\"窑口\": \"景德镇御窑厂\", \"釉色\": \"粉彩\"}', 2, '2025-12-16 10:27:30', '2025-12-16 17:41:46', 0);
INSERT INTO `cultural_relics` VALUES (11, '快雪时晴帖', '魏晋南北朝', '书画', 3, '传为江南地区', '台北故宫博物院', '中国书法史上著名行书作品之一，流传影响深远。', '{\"作者\": \"王羲之\", \"横向尺寸\": \"14.8cm\", \"纵向尺寸\": \"23.7cm\"}', 3, '2025-12-16 10:27:30', '2025-12-16 17:41:53', 0);
INSERT INTO `cultural_relics` VALUES (12, '韩熙载夜宴图', '魏晋南北朝', '书画', 3, '江南地区', '故宫博物院', '分段描绘韩熙载夜宴情景的大型长卷，刻画人物细腻。', '{\"作者\": \"顾闳中\", \"横向尺寸\": \"335.5cm\", \"纵向尺寸\": \"28.7cm\"}', 3, '2025-12-16 10:27:30', '2025-12-16 17:41:56', 0);
INSERT INTO `cultural_relics` VALUES (13, '清明上河图', '宋元', '书画', 3, '东京汴梁(今河南开封)', '故宫博物院', '巨幅风俗画名作，细致描绘北宋都城繁荣景象。', '{\"作者\": \"张择端\", \"横向尺寸\": \"528.7cm\", \"纵向尺寸\": \"24.8cm\"}', 3, '2025-12-16 10:27:30', '2025-12-16 17:41:58', 0);
INSERT INTO `cultural_relics` VALUES (14, '富春山居图(剩山图)', '宋元', '书画', 3, '浙江富春江流域', '浙江省博物馆', '元代山水画巅峰之作之一，构图与笔墨极具影响力。', '{\"作者\": \"黄公望\", \"横向尺寸\": \"636.9cm\", \"纵向尺寸\": \"33cm\"}', 3, '2025-12-16 10:27:30', '2025-12-16 17:42:01', 0);
INSERT INTO `cultural_relics` VALUES (15, '洛神赋图', '魏晋南北朝', '书画', 3, '传为江南地区', '故宫博物院', '以曹植《洛神赋》为题材的长卷画作，人物形象飘逸。', '{\"作者\": \"顾恺之\", \"横向尺寸\": \"572.8cm\", \"纵向尺寸\": \"27.1cm\"}', 3, '2025-12-16 10:27:30', '2025-12-16 17:42:05', 0);
INSERT INTO `cultural_relics` VALUES (16, '良渚文化玉琮', '新石器时代晚期', '玉器', 4, '浙江省杭州市余杭区良渚遗址', '浙江省博物馆', '良渚文化代表性礼器，外方内圆，雕刻神人兽面纹。', '{\"工艺\": \"浅浮雕\", \"玉种\": \"良渚玉\"}', 2, '2025-12-16 10:27:30', '2025-12-16 17:42:23', 0);
INSERT INTO `cultural_relics` VALUES (17, '汉代龙纹玉佩', '秦汉', '玉器', 4, '湖北省江陵县', '湖北省博物馆', '汉代龙纹玉佩，雕工精细，线条流畅。', '{\"工艺\": \"透雕\", \"玉种\": \"和田玉\"}', 2, '2025-12-16 10:27:30', '2025-12-16 17:42:25', 0);
INSERT INTO `cultural_relics` VALUES (18, '唐代团花纹金带扣', '隋唐', '金银器', 1, '陕西省西安市', '陕西历史博物馆', '金质腰带扣饰，浮雕团花纹饰，展现唐代金工技艺。', '{\"重量\": \"120g\", \"高度\": \"6.8cm\"}', 3, '2025-12-16 10:27:30', '2025-12-16 17:42:38', 0);
INSERT INTO `cultural_relics` VALUES (19, '战国错金银铜壶', '商周', '青铜', 1, '湖北省随州市', '湖北省博物馆', '铜壶表面错嵌金银纹饰，工艺复杂精致。', '{\"重量\": \"3.2kg\", \"高度\": \"27cm\"}', 3, '2025-12-16 10:27:30', '2025-12-16 17:38:37', 0);
INSERT INTO `cultural_relics` VALUES (20, '宋代梅花形银饰', '宋元', '金银器', 1, '河南省开封市', '河南博物院', '梅花形银质首饰，工艺细腻，风格典雅。', '{\"重量\": \"25g\", \"高度\": \"3.5cm\"}', 2, '2025-12-16 10:27:30', '2025-12-16 17:43:09', 0);

-- ----------------------------
-- Table structure for entity_relations
-- ----------------------------
DROP TABLE IF EXISTS `entity_relations`;
CREATE TABLE `entity_relations`  (
  `relation_id` int NOT NULL AUTO_INCREMENT COMMENT '关系ID',
  `relation_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '关系名称',
  `source_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '源实体类型',
  `source_id` int NOT NULL COMMENT '源实体ID',
  `target_string` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '目标实体名称/值',
  `relation_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '描述',
  `created_by` bigint NOT NULL COMMENT '创建者ID',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`relation_id`) USING BTREE,
  INDEX `idx_source`(`source_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '实体关系表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of entity_relations
-- ----------------------------
INSERT INTO `entity_relations` VALUES (1, '出土于', 'RELIC', 1, '河南省安阳市小屯村殷墟', '后母戊鼎的出土地点', 2, '2025-12-16 10:27:30', 0);
INSERT INTO `entity_relations` VALUES (2, '收藏于', 'RELIC', 1, '中国国家博物馆', '后母戊鼎现藏单位', 2, '2025-12-16 10:27:30', 0);
INSERT INTO `entity_relations` VALUES (3, '同一遗址出土', 'RELIC', 3, '湖北省随州市擂鼓墩', '曾侯乙编钟相关遗址', 3, '2025-12-16 10:27:30', 0);
INSERT INTO `entity_relations` VALUES (4, '属于类型', 'RELIC', 4, '陶俑', '秦兵马俑所属类别', 2, '2025-12-16 10:27:30', 0);

-- ----------------------------
-- Table structure for feedback
-- ----------------------------
DROP TABLE IF EXISTS `feedback`;
CREATE TABLE `feedback`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '反馈ID',
  `user_id` bigint NOT NULL COMMENT '提交用户ID',
  `feedback_type` int NULL DEFAULT 0 COMMENT '类型：0-建议，1-问题，2-需求',
  `feedback_title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标题',
  `feedback_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '内容',
  `contact_info` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系方式',
  `submitted_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  `status` int NULL DEFAULT 0 COMMENT '状态：0-待处理，1-处理中，2-已解决',
  `processed_by` bigint NULL DEFAULT NULL COMMENT '处理人ID',
  `processed_at` datetime NULL DEFAULT NULL COMMENT '处理时间',
  `process_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '处理回复',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户反馈表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of feedback
-- ----------------------------
INSERT INTO `feedback` VALUES (1, 4, 0, '搜索结果不够精准', '在搜索“青铜器”时，结果列表中文物年代跨度较大，希望可以增加按年代筛选功能。', 'student@example.com', '2025-12-16 10:27:30', 2, 2, '2025-01-12 09:00:00', '已记录需求，并计划在下个版本增加年代过滤条件。');
INSERT INTO `feedback` VALUES (2, 5, 1, '文物描述疑似有误', '四羊方尊的出土地信息与我在教材中看到的不同，建议核实。', 'visitor@example.com', '2025-12-16 10:27:30', 2, 3, '2025-01-13 14:20:00', '已与专家核对，并在后台修正相关描述。');
INSERT INTO `feedback` VALUES (3, 4, 2, '希望增加课堂模式', '建议增加“课堂演示模式”，可以一键隐藏敏感信息，只展示关键知识点。', 'student@example.com', '2025-12-16 10:27:30', 1, 1, '2025-01-14 10:30:00', '正在调研该需求的实现方式，计划在后续版本中试点。');
INSERT INTO `feedback` VALUES (4, 3, 0, '专家审核界面交互建议', '审核列表中希望增加“最近提交”的排序方式，以便快速查看最新申请。', 'expert@example.com', '2025-12-16 10:27:30', 0, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for feedback_evaluation
-- ----------------------------
DROP TABLE IF EXISTS `feedback_evaluation`;
CREATE TABLE `feedback_evaluation`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `feedback_id` bigint NOT NULL COMMENT '关联反馈ID',
  `rating` int NULL DEFAULT 5 COMMENT '评分(1-5)',
  `comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '评价内容',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_feedback`(`feedback_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '反馈评价表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of feedback_evaluation
-- ----------------------------
INSERT INTO `feedback_evaluation` VALUES (1, 1, 5, '处理及时，功能优化后体验很好。', '2025-12-16 10:27:30');
INSERT INTO `feedback_evaluation` VALUES (2, 2, 4, '说明较为详细，希望后续能开放修改记录查看功能。', '2025-12-16 10:27:30');
INSERT INTO `feedback_evaluation` VALUES (3, 3, 5, '期待课堂模式尽快上线，对教学非常有帮助。', '2025-12-16 10:27:30');

-- ----------------------------
-- Table structure for knowledge_card
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_card`;
CREATE TABLE `knowledge_card`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `relic_id` int NOT NULL COMMENT '文物ID',
  `card_json` json NOT NULL COMMENT '完整知识卡片数据(JSON)',
  `version` int NULL DEFAULT 1 COMMENT '版本号',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_relic`(`relic_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '知识卡片缓存表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of knowledge_card
-- ----------------------------
INSERT INTO `knowledge_card` VALUES (1, 1, '{\"tags\": [\"商代\", \"青铜器\", \"礼器\"], \"title\": \"后母戊鼎知识卡片\", \"summary\": \"商代晚期大型青铜礼器代表作，现藏中国国家博物馆。\"}', 1, '2025-12-16 10:27:30');
INSERT INTO `knowledge_card` VALUES (2, 4, '{\"tags\": [\"秦代\", \"兵马俑\", \"陵墓\"], \"title\": \"秦始皇兵马俑步兵俑知识卡片\", \"summary\": \"秦始皇陵陪葬坑中陶俑之一，用于再现秦军军阵。\"}', 1, '2025-12-16 10:27:30');
INSERT INTO `knowledge_card` VALUES (3, 13, '{\"tags\": [\"北宋\", \"风俗画\", \"张择端\"], \"title\": \"清明上河图知识卡片\", \"summary\": \"描绘北宋东京汴梁繁荣市井生活的长卷画作。\"}', 1, '2025-12-16 10:27:30');

-- ----------------------------
-- Table structure for relic_multi_mode
-- ----------------------------
DROP TABLE IF EXISTS `relic_multi_mode`;
CREATE TABLE `relic_multi_mode`  (
  `resource_id` int NOT NULL AUTO_INCREMENT COMMENT '资源ID',
  `relics_id` int NOT NULL COMMENT '文物ID',
  `resource_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '资源类型(image/video/3d_model)',
  `resource_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '资源地址或内容',
  `created_by` bigint NOT NULL COMMENT '创建者ID',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`resource_id`) USING BTREE,
  INDEX `idx_relic`(`relics_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '多模态资源表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of relic_multi_mode
-- ----------------------------
INSERT INTO `relic_multi_mode` VALUES (1, 1, 'image', '/static/images/relics/houmuwu_ding_1.jpg', 2, '2025-12-16 10:27:30', '2025-12-16 10:27:30', 0);
INSERT INTO `relic_multi_mode` VALUES (2, 2, 'image', '/static/images/relics/siyang_fangzun_1.jpg', 2, '2025-12-16 10:27:30', '2025-12-16 10:27:30', 0);
INSERT INTO `relic_multi_mode` VALUES (3, 3, 'image', '/static/images/relics/zenghouyi_bianzhong_1.jpg', 3, '2025-12-16 10:27:30', '2025-12-16 10:27:30', 0);
INSERT INTO `relic_multi_mode` VALUES (4, 4, 'image', '/static/images/relics/qin_bingmayong_1.jpg', 2, '2025-12-16 10:27:30', '2025-12-16 10:27:30', 0);
INSERT INTO `relic_multi_mode` VALUES (5, 5, 'image', '/static/images/relics/tangsancai_camel_1.jpg', 2, '2025-12-16 10:27:30', '2025-12-16 10:27:30', 0);
INSERT INTO `relic_multi_mode` VALUES (6, 10, 'image', '/static/images/relics/qianlong_tiankqiuping_1.jpg', 3, '2025-12-16 10:27:30', '2025-12-16 10:27:30', 0);
INSERT INTO `relic_multi_mode` VALUES (7, 13, 'image', '/static/images/relics/qingmingshanghetu_1.jpg', 3, '2025-12-16 10:27:30', '2025-12-16 10:27:30', 0);
INSERT INTO `relic_multi_mode` VALUES (8, 16, 'image', '/static/images/relics/liangzhu_yucong_1.jpg', 2, '2025-12-16 10:27:30', '2025-12-16 10:27:30', 0);
INSERT INTO `relic_multi_mode` VALUES (9, 19, 'image', '/static/images/relics/warring_states_bronze_1.jpg', 3, '2025-12-16 10:27:30', '2025-12-16 10:27:30', 0);
INSERT INTO `relic_multi_mode` VALUES (10, 20, 'image', '/static/images/relics/song_silver_flower_1.jpg', 2, '2025-12-16 10:27:30', '2025-12-16 10:27:30', 0);

-- ----------------------------
-- Table structure for relics_type
-- ----------------------------
DROP TABLE IF EXISTS `relics_type`;
CREATE TABLE `relics_type`  (
  `relics_type_id` int NOT NULL AUTO_INCREMENT COMMENT '文物类型ID',
  `type_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '类型名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '类型描述',
  `type_fields` json NULL COMMENT '该类型的动态字段定义(JSON)',
  `created_by` bigint NOT NULL COMMENT '创建者ID',
  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`relics_type_id`) USING BTREE,
  UNIQUE INDEX `uk_type_name`(`type_name` ASC, `is_deleted` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文物类型表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of relics_type
-- ----------------------------
INSERT INTO `relics_type` VALUES (1, '青铜器', '以青铜为主要材质的礼器、兵器等', '[{\"name\": \"高度\", \"type\": \"text\", \"required\": false, \"description\": \"文物高度（cm）\"}, {\"name\": \"重量\", \"type\": \"text\", \"required\": false, \"description\": \"文物重量（kg 或 g）\"}]', 1, '2025-12-16 10:27:30', '2025-12-16 10:27:30', 0);
INSERT INTO `relics_type` VALUES (2, '陶瓷器', '各历史时期烧制的陶器与瓷器', '[{\"name\": \"窑口\", \"type\": \"text\", \"required\": false, \"description\": \"烧制窑口名称\"}, {\"name\": \"釉色\", \"type\": \"text\", \"required\": false, \"description\": \"釉色名称\"}]', 1, '2025-12-16 10:27:30', '2025-12-16 10:27:30', 0);
INSERT INTO `relics_type` VALUES (3, '书画', '纸本、绢本等绘画与书法作品', '[{\"name\": \"作者\", \"type\": \"text\", \"required\": true, \"description\": \"作品作者\"}, {\"name\": \"纵向尺寸\", \"type\": \"text\", \"required\": false, \"description\": \"纵向尺寸（cm）\"}, {\"name\": \"横向尺寸\", \"type\": \"text\", \"required\": false, \"description\": \"横向尺寸（cm）\"}]', 1, '2025-12-16 10:27:30', '2025-12-16 10:27:30', 0);
INSERT INTO `relics_type` VALUES (4, '玉器', '以玉石为主要材质的工艺品与礼器', '[{\"name\": \"玉种\", \"type\": \"text\", \"required\": false, \"description\": \"玉石种类\"}, {\"name\": \"工艺\", \"type\": \"text\", \"required\": false, \"description\": \"主要加工工艺\"}]', 1, '2025-12-16 10:27:30', '2025-12-16 10:27:30', 0);

-- ----------------------------
-- Table structure for search_record
-- ----------------------------
DROP TABLE IF EXISTS `search_record`;
CREATE TABLE `search_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `keyword` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '搜索关键词',
  `user_id` bigint NULL DEFAULT NULL COMMENT '搜索用户',
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '搜索时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_keyword`(`keyword` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '搜索记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of search_record
-- ----------------------------
INSERT INTO `search_record` VALUES (1, '青铜器', 4, '127.0.0.1', '2025-12-16 10:27:30');
INSERT INTO `search_record` VALUES (2, '兵马俑', 4, '127.0.0.1', '2025-12-16 10:27:30');
INSERT INTO `search_record` VALUES (3, '唐三彩', 5, '127.0.0.1', '2025-12-16 10:27:30');
INSERT INTO `search_record` VALUES (4, '汝窑', 3, '127.0.0.1', '2025-12-16 10:27:30');
INSERT INTO `search_record` VALUES (5, '良渚文化', 2, '127.0.0.1', '2025-12-16 10:27:30');

-- ----------------------------
-- Table structure for sys_file
-- ----------------------------
DROP TABLE IF EXISTS `sys_file`;
CREATE TABLE `sys_file`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '文件ID',
  `file_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '原始文件名',
  `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件存储路径/URL',
  `file_size` bigint NULL DEFAULT 0 COMMENT '文件大小(字节)',
  `file_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件类型(后缀)',
  `content_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'MIME类型',
  `uploaded_by` bigint NULL DEFAULT NULL COMMENT '上传人ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '附件/文件表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_file
-- ----------------------------

-- ----------------------------
-- Table structure for sys_message
-- ----------------------------
DROP TABLE IF EXISTS `sys_message`;
CREATE TABLE `sys_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `sender_id` bigint NULL DEFAULT 0 COMMENT '发送者ID(0代表系统)',
  `receiver_id` bigint NOT NULL COMMENT '接收者ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '消息标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息内容',
  `type` int NULL DEFAULT 0 COMMENT '消息类型：0-系统通知，1-待办提醒',
  `is_read` tinyint(1) NULL DEFAULT 0 COMMENT '是否已读',
  `read_time` datetime NULL DEFAULT NULL COMMENT '阅读时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_receiver`(`receiver_id` ASC, `is_read` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_message
-- ----------------------------

-- ----------------------------
-- Table structure for sys_operation_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_operation_log`;
CREATE TABLE `sys_operation_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '操作用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作用户名',
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `module` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作模块',
  `operation` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作类型',
  `method` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '请求方法',
  `params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '请求参数',
  `result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '返回结果',
  `status` int NULL DEFAULT 0 COMMENT '操作状态：0-成功，1-失败',
  `error_msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '错误信息',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_operation_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限名称',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限标识',
  `type` int NULL DEFAULT 1 COMMENT '类型：1-菜单，2-按钮',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父权限ID',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '路由路径',
  `component` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '前端组件',
  `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图标',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_permission
-- ----------------------------
INSERT INTO `sys_permission` VALUES (1, '仪表盘', 'dashboard:view', 1, 0, '/dashboard', 'Dashboard', 'dashboard', 1, '2025-12-16 10:27:30');
INSERT INTO `sys_permission` VALUES (2, '文物管理', 'relic:manage', 1, 0, '/artifacts', 'ArtifactList', 'database', 2, '2025-12-16 10:27:30');
INSERT INTO `sys_permission` VALUES (3, '文物新增', 'relic:add', 2, 2, NULL, NULL, NULL, 3, '2025-12-16 10:27:30');
INSERT INTO `sys_permission` VALUES (4, '文物编辑', 'relic:edit', 2, 2, NULL, NULL, NULL, 4, '2025-12-16 10:27:30');
INSERT INTO `sys_permission` VALUES (5, '文物审核', 'relic:audit', 2, 2, NULL, NULL, NULL, 5, '2025-12-16 10:27:30');
INSERT INTO `sys_permission` VALUES (6, '用户管理', 'user:manage', 1, 0, '/system/users', 'UserList', 'user', 6, '2025-12-16 10:27:30');
INSERT INTO `sys_permission` VALUES (7, '反馈处理', 'feedback:manage', 1, 0, '/feedback', 'FeedbackList', 'message', 7, '2025-12-16 10:27:30');
INSERT INTO `sys_permission` VALUES (8, 'AI助手', 'ai:chat', 1, 0, '/ai', 'AiChat', 'robot', 8, '2025-12-16 10:27:30');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色代码(如: admin, curator)',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `status` int NULL DEFAULT 0 COMMENT '状态：0-正常，1-禁用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_code`(`code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '系统管理员', 'admin', '系统最高管理员，拥有全部权限', 1, 0, '2025-12-16 10:27:30', '2025-12-16 10:27:30');
INSERT INTO `sys_role` VALUES (2, '文物馆员', 'curator', '负责日常文物管理与审核', 2, 0, '2025-12-16 10:27:30', '2025-12-16 10:27:30');
INSERT INTO `sys_role` VALUES (3, '文物专家', 'expert', '负责文物专业审核与知识补充', 3, 0, '2025-12-16 10:27:30', '2025-12-16 10:27:30');
INSERT INTO `sys_role` VALUES (4, '普通用户', 'user', '普通访问用户，主要用于浏览与反馈', 4, 0, '2025-12-16 10:27:30', '2025-12-16 10:27:30');

-- ----------------------------
-- Table structure for sys_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `permission_id` bigint NOT NULL COMMENT '权限ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_perm`(`role_id` ASC, `permission_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色权限关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_permission
-- ----------------------------
INSERT INTO `sys_role_permission` VALUES (1, 1, 1);
INSERT INTO `sys_role_permission` VALUES (2, 1, 2);
INSERT INTO `sys_role_permission` VALUES (3, 1, 3);
INSERT INTO `sys_role_permission` VALUES (4, 1, 4);
INSERT INTO `sys_role_permission` VALUES (5, 1, 5);
INSERT INTO `sys_role_permission` VALUES (6, 1, 6);
INSERT INTO `sys_role_permission` VALUES (7, 1, 7);
INSERT INTO `sys_role_permission` VALUES (8, 1, 8);
INSERT INTO `sys_role_permission` VALUES (9, 2, 1);
INSERT INTO `sys_role_permission` VALUES (10, 2, 2);
INSERT INTO `sys_role_permission` VALUES (11, 2, 3);
INSERT INTO `sys_role_permission` VALUES (12, 2, 4);
INSERT INTO `sys_role_permission` VALUES (13, 2, 5);
INSERT INTO `sys_role_permission` VALUES (14, 2, 7);
INSERT INTO `sys_role_permission` VALUES (15, 2, 8);
INSERT INTO `sys_role_permission` VALUES (16, 3, 1);
INSERT INTO `sys_role_permission` VALUES (17, 3, 2);
INSERT INTO `sys_role_permission` VALUES (18, 3, 4);
INSERT INTO `sys_role_permission` VALUES (19, 3, 5);
INSERT INTO `sys_role_permission` VALUES (20, 3, 8);
INSERT INTO `sys_role_permission` VALUES (21, 4, 1);
INSERT INTO `sys_role_permission` VALUES (22, 4, 2);
INSERT INTO `sys_role_permission` VALUES (23, 4, 8);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像',
  `role` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'user' COMMENT '角色标识(冗余字段)',
  `status` int NULL DEFAULT 0 COMMENT '状态：0-正常，1-禁用',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', 'admin@example.com', '13800000001', '系统管理员', NULL, 'admin', 1, '2025-12-16 17:46:34', '2025-12-16 10:27:30', '2025-12-16 17:58:15');
INSERT INTO `sys_user` VALUES (2, 'curator_zhang', '123456', 'curator@example.com', '13800000002', '张馆员', NULL, 'advanced', 1, NULL, '2025-12-16 10:27:30', '2025-12-16 17:27:38');
INSERT INTO `sys_user` VALUES (3, 'expert_li', 'e10adc3949ba59abbe56e057f20f883e', 'expert@example.com', '13800000003', '李专家', NULL, 'advanced', 1, NULL, '2025-12-16 10:27:30', '2025-12-16 17:27:28');
INSERT INTO `sys_user` VALUES (4, 'student_wang', 'e10adc3949ba59abbe56e057f20f883e', 'student@example.com', '13800000004', '王同学', NULL, 'user', 1, NULL, '2025-12-16 10:27:30', '2025-12-16 17:20:09');
INSERT INTO `sys_user` VALUES (5, 'visitor_demo', '123456', 'visitor@example.com', '13800000005', '访客用户', NULL, 'user', 1, NULL, '2025-12-16 10:27:30', '2025-12-16 16:34:46');

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_role`(`user_id` ASC, `role_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1, 1);
INSERT INTO `sys_user_role` VALUES (2, 2, 2);
INSERT INTO `sys_user_role` VALUES (3, 3, 3);
INSERT INTO `sys_user_role` VALUES (4, 4, 4);
INSERT INTO `sys_user_role` VALUES (5, 5, 4);

SET FOREIGN_KEY_CHECKS = 1;
