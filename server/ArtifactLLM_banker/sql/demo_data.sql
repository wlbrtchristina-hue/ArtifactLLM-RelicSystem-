SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE feedback_evaluation;
TRUNCATE TABLE feedback;
TRUNCATE TABLE knowledge_card;
TRUNCATE TABLE search_record;
TRUNCATE TABLE ai_chat_history;
TRUNCATE TABLE entity_relations;
TRUNCATE TABLE audit;
TRUNCATE TABLE audit_type;
TRUNCATE TABLE relic_multi_mode;
TRUNCATE TABLE cultural_relics;
TRUNCATE TABLE relics_type;
TRUNCATE TABLE sys_role_permission;
TRUNCATE TABLE sys_user_role;
TRUNCATE TABLE sys_permission;
TRUNCATE TABLE sys_role;
TRUNCATE TABLE sys_message;
TRUNCATE TABLE sys_operation_log;
TRUNCATE TABLE sys_file;
TRUNCATE TABLE sys_user;

INSERT INTO sys_role (id, name, code, description, sort_order, status)
VALUES
(1, '系统管理员', 'admin', '系统最高管理员，拥有全部权限', 1, 0),
(2, '文物馆员', 'curator', '负责日常文物管理与审核', 2, 0),
(3, '文物专家', 'expert', '负责文物专业审核与知识补充', 3, 0),
(4, '普通用户', 'user', '普通访问用户，主要用于浏览与反馈', 4, 0);

INSERT INTO sys_permission (id, name, code, type, parent_id, path, component, icon, sort_order)
VALUES
(1, '仪表盘', 'dashboard:view', 1, 0, '/dashboard', 'Dashboard', 'dashboard', 1),
(2, '文物管理', 'relic:manage', 1, 0, '/artifacts', 'ArtifactList', 'database', 2),
(3, '文物新增', 'relic:add', 2, 2, NULL, NULL, NULL, 3),
(4, '文物编辑', 'relic:edit', 2, 2, NULL, NULL, NULL, 4),
(5, '文物审核', 'relic:audit', 2, 2, NULL, NULL, NULL, 5),
(6, '用户管理', 'user:manage', 1, 0, '/system/users', 'UserList', 'user', 6),
(7, '反馈处理', 'feedback:manage', 1, 0, '/feedback', 'FeedbackList', 'message', 7),
(8, 'AI助手', 'ai:chat', 1, 0, '/ai', 'AiChat', 'robot', 8);

INSERT INTO sys_user (id, username, password, email, phone, real_name, avatar, role, status)
VALUES
(1, 'admin', '123456', 'admin@example.com', '13800000001', '系统管理员', NULL, 'admin', 0),
(2, 'curator_zhang', '123456', 'curator@example.com', '13800000002', '张馆员', NULL, 'curator', 0),
(3, 'expert_li', '123456', 'expert@example.com', '13800000003', '李专家', NULL, 'expert', 0),
(4, 'student_wang', '123456', 'student@example.com', '13800000004', '王同学', NULL, 'user', 0),
(5, 'visitor_demo', '123456', 'visitor@example.com', '13800000005', '访客用户', NULL, 'user', 0);

INSERT INTO sys_user_role (user_id, role_id)
VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 4),
(5, 4);

INSERT INTO sys_role_permission (role_id, permission_id)
VALUES
(1, 1),
(1, 2),
(1, 3),
(1, 4),
(1, 5),
(1, 6),
(1, 7),
(1, 8),
(2, 1),
(2, 2),
(2, 3),
(2, 4),
(2, 5),
(2, 7),
(2, 8),
(3, 1),
(3, 2),
(3, 4),
(3, 5),
(3, 8),
(4, 1),
(4, 2),
(4, 8);

INSERT INTO relics_type (relics_type_id, type_name, description, type_fields, created_by, is_deleted)
VALUES
(1, '青铜器', '以青铜为主要材质的礼器、兵器等', '[{"name":"高度","type":"text","description":"文物高度（cm）","required":false},{"name":"重量","type":"text","description":"文物重量（kg 或 g）","required":false}]', 1, 0),
(2, '陶瓷器', '各历史时期烧制的陶器与瓷器', '[{"name":"窑口","type":"text","description":"烧制窑口名称","required":false},{"name":"釉色","type":"text","description":"釉色名称","required":false}]', 1, 0),
(3, '书画', '纸本、绢本等绘画与书法作品', '[{"name":"作者","type":"text","description":"作品作者","required":true},{"name":"纵向尺寸","type":"text","description":"纵向尺寸（cm）","required":false},{"name":"横向尺寸","type":"text","description":"横向尺寸（cm）","required":false}]', 1, 0),
(4, '玉器', '以玉石为主要材质的工艺品与礼器', '[{"name":"玉种","type":"text","description":"玉石种类","required":false},{"name":"工艺","type":"text","description":"主要加工工艺","required":false}]', 1, 0);

INSERT INTO cultural_relics (relics_id, relics_name, era, material, relics_type_id, discovery_site, current_location, description, custom_fields, created_by, is_deleted)
VALUES
(1, '后母戊鼎', '商代晚期', '青铜', 1, '河南省安阳市小屯村殷墟', '国家博物馆', '商代晚期青铜礼器中的代表作，体量巨大，工艺精湛。', '{"高度":"133cm","重量":"832.84kg"}', 2, 0),
(2, '四羊方尊', '商代晚期', '青铜', 1, '湖南省宁乡县月山铺', '国家博物馆', '造型复杂、纹饰华丽的方尊，四角立羊，结构巧妙。', '{"高度":"58cm","重量":"34.5kg"}', 2, 0),
(3, '曾侯乙编钟', '战国早期', '青铜', 1, '湖北省随州市擂鼓墩曾侯乙墓', '湖北省博物馆', '大型编钟乐器组合，代表先秦礼乐文明的高峰。', '{"高度":"273cm","重量":"2500kg"}', 3, 0),
(4, '秦始皇兵马俑步兵俑', '秦代', '陶士', 2, '陕西省西安市临潼区骊山镇', '秦始皇帝陵博物院', '姿态各异的兵马俑之一，展现秦军军容。', '{"窑口":"秦始皇陵陪葬坑","釉色":"原为彩绘，现多脱落"}', 2, 0),
(5, '唐三彩骆驼俑', '唐代', '陶', 2, '河南省洛阳市', '洛阳博物馆', '唐代三彩陶俑代表作品之一，造型生动，色彩绚丽。', '{"窑口":"洛阳窑","釉色":"黄绿白三彩"}', 2, 0),
(6, '汝窑天青釉洗', '北宋', '瓷', 2, '河南省宝丰清凉寺汝官窑遗址', '台北故宫博物院', '传世稀少的汝窑器物，釉色温润如玉。', '{"窑口":"汝窑","釉色":"天青色"}', 3, 0),
(7, '官窑青釉葵口碗', '南宋', '瓷', 2, '浙江省杭州市', '浙江省博物馆', '南宋官窑代表器形之一，釉色粉青，开片细密。', '{"窑口":"南宋官窑","釉色":"粉青釉"}', 3, 0),
(8, '元青花萧何月下追韩信图梅瓶', '元代', '瓷', 2, '江西省景德镇', '上海博物馆', '经典青花故事纹器物，绘画生动，构图饱满。', '{"窑口":"景德镇窑","釉色":"白釉蓝花"}', 3, 0),
(9, '明成化斗彩鸡缸杯', '明代成化年间', '瓷', 2, '江西省景德镇', '台北故宫博物院', '成化斗彩名品之一，胎釉精细，纹饰清新。', '{"窑口":"景德镇御窑厂","釉色":"斗彩"}', 3, 0),
(10, '清乾隆粉彩九桃天球瓶', '清代乾隆年间', '瓷', 2, '江西省景德镇', '故宫博物院', '粉彩瓷器代表作品，寓意多福多寿。', '{"窑口":"景德镇御窑厂","釉色":"粉彩"}', 2, 0),
(11, '快雪时晴帖', '东晋(传王羲之)', '纸本', 3, '传为江南地区', '台北故宫博物院', '中国书法史上著名行书作品之一，流传影响深远。', '{"作者":"王羲之","纵向尺寸":"23.7cm","横向尺寸":"14.8cm"}', 3, 0),
(12, '韩熙载夜宴图', '五代南唐', '绢本设色', 3, '江南地区', '故宫博物院', '分段描绘韩熙载夜宴情景的大型长卷，刻画人物细腻。', '{"作者":"顾闳中","纵向尺寸":"28.7cm","横向尺寸":"335.5cm"}', 3, 0),
(13, '清明上河图', '北宋', '绢本设色', 3, '东京汴梁(今河南开封)', '故宫博物院', '巨幅风俗画名作，细致描绘北宋都城繁荣景象。', '{"作者":"张择端","纵向尺寸":"24.8cm","横向尺寸":"528.7cm"}', 3, 0),
(14, '富春山居图(剩山图)', '元代', '纸本水墨', 3, '浙江富春江流域', '浙江省博物馆', '元代山水画巅峰之作之一，构图与笔墨极具影响力。', '{"作者":"黄公望","纵向尺寸":"33cm","横向尺寸":"636.9cm"}', 3, 0),
(15, '洛神赋图', '东晋(传顾恺之)', '绢本设色', 3, '传为江南地区', '故宫博物院', '以曹植《洛神赋》为题材的长卷画作，人物形象飘逸。', '{"作者":"顾恺之","纵向尺寸":"27.1cm","横向尺寸":"572.8cm"}', 3, 0),
(16, '良渚文化玉琮', '新石器时代晚期', '玉石', 4, '浙江省杭州市余杭区良渚遗址', '浙江省博物馆', '良渚文化代表性礼器，外方内圆，雕刻神人兽面纹。', '{"玉种":"良渚玉","工艺":"浅浮雕"}', 2, 0),
(17, '汉代龙纹玉佩', '西汉', '玉石', 4, '湖北省江陵县', '湖北省博物馆', '汉代龙纹玉佩，雕工精细，线条流畅。', '{"玉种":"和田玉","工艺":"透雕"}', 2, 0),
(18, '唐代团花纹金带扣', '唐代', '金', 1, '陕西省西安市', '陕西历史博物馆', '金质腰带扣饰，浮雕团花纹饰，展现唐代金工技艺。', '{"高度":"6.8cm","重量":"120g"}', 3, 0),
(19, '战国错金银铜壶', '战国', '青铜', 1, '湖北省随州市', '湖北省博物馆', '铜壶表面错嵌金银纹饰，工艺复杂精致。', '{"高度":"27cm","重量":"3.2kg"}', 3, 0),
(20, '宋代梅花形银饰', '北宋', '银', 1, '河南省开封市', '河南博物院', '梅花形银质首饰，工艺细腻，风格典雅。', '{"高度":"3.5cm","重量":"25g"}', 2, 0);

INSERT INTO relic_multi_mode (resource_id, relics_id, resource_type, resource_content, created_by, is_deleted)
VALUES
(1, 1, 'image', '/static/images/relics/houmuwu_ding_1.jpg', 2, 0),
(2, 2, 'image', '/static/images/relics/siyang_fangzun_1.jpg', 2, 0),
(3, 3, 'image', '/static/images/relics/zenghouyi_bianzhong_1.jpg', 3, 0),
(4, 4, 'image', '/static/images/relics/qin_bingmayong_1.jpg', 2, 0),
(5, 5, 'image', '/static/images/relics/tangsancai_camel_1.jpg', 2, 0),
(6, 10, 'image', '/static/images/relics/qianlong_tiankqiuping_1.jpg', 3, 0),
(7, 13, 'image', '/static/images/relics/qingmingshanghetu_1.jpg', 3, 0),
(8, 16, 'image', '/static/images/relics/liangzhu_yucong_1.jpg', 2, 0),
(9, 19, 'image', '/static/images/relics/warring_states_bronze_1.jpg', 3, 0),
(10, 20, 'image', '/static/images/relics/song_silver_flower_1.jpg', 2, 0);

INSERT INTO audit_type (audit_type_id, audit_type_name, audit_content_fields)
VALUES
(1, '新增文物', 'relics_name, era, material, relics_type_id'),
(2, '修改文物', 'relics_id, modified_fields'),
(3, '删除文物', 'relics_id, reason');

INSERT INTO audit (audit_id, audit_status, audit_type_id, audit_data, created_by, auditor_id, audit_time, reject_reason)
VALUES
(1, 'approved', 1, '{"relics_id":21,"relics_name":"演示新增文物","action":"create"}', 2, 1, '2025-01-10 10:00:00', NULL),
(2, 'rejected', 2, '{"relics_id":5,"field":"description","action":"update"}', 3, 1, '2025-01-11 15:30:00', '描述内容与史实不符'),
(3, 'pending', 2, '{"relics_id":3,"field":"current_location","action":"update"}', 2, NULL, NULL, NULL);

INSERT INTO entity_relations (relation_id, relation_name, source_type, source_id, target_string, relation_description, created_by, is_deleted)
VALUES
(1, '出土于', 'RELIC', 1, '河南省安阳市小屯村殷墟', '后母戊鼎的出土地点', 2, 0),
(2, '收藏于', 'RELIC', 1, '中国国家博物馆', '后母戊鼎现藏单位', 2, 0),
(3, '同一遗址出土', 'RELIC', 3, '湖北省随州市擂鼓墩', '曾侯乙编钟相关遗址', 3, 0),
(4, '属于类型', 'RELIC', 4, '陶俑', '秦兵马俑所属类别', 2, 0);

INSERT INTO ai_chat_history (session_id, user_id, user_input, ai_response, model)
VALUES
('demo-session-1', 4, '帮我概括一下后母戊鼎的历史价值。', '后母戊鼎是商代晚期最大最重的青铜礼器之一，体现了商王朝高度发达的青铜铸造技术与礼制文化，是研究商代政治、宗教与铸造工艺的重要实物。', 'gpt-4.1-mini'),
('demo-session-1', 4, '和四羊方尊相比，它们有什么共同点？', '两者同属商代晚期重要青铜礼器，都体现高超铸造工艺与复杂纹饰，用于宗教祭祀场合，在造型和艺术风格上具有浓厚的商代特色。', 'gpt-4.1-mini'),
('demo-session-2', 5, '适合作为课堂案例的唐代文物有哪些？', '可以选择唐三彩骆驼俑展示丝绸之路贸易与丧葬习俗，也可以结合唐代金银器、壁画等文物，从多角度介绍唐代开放多元的文化面貌。', 'gpt-4.1-mini');

INSERT INTO search_record (keyword, user_id, ip_address)
VALUES
('青铜器', 4, '127.0.0.1'),
('兵马俑', 4, '127.0.0.1'),
('唐三彩', 5, '127.0.0.1'),
('汝窑', 3, '127.0.0.1'),
('良渚文化', 2, '127.0.0.1');

INSERT INTO knowledge_card (relic_id, card_json, version)
VALUES
(1, '{"title":"后母戊鼎知识卡片","summary":"商代晚期大型青铜礼器代表作，现藏中国国家博物馆。","tags":["商代","青铜器","礼器"]}', 1),
(4, '{"title":"秦始皇兵马俑步兵俑知识卡片","summary":"秦始皇陵陪葬坑中陶俑之一，用于再现秦军军阵。","tags":["秦代","兵马俑","陵墓"]}', 1),
(13, '{"title":"清明上河图知识卡片","summary":"描绘北宋东京汴梁繁荣市井生活的长卷画作。","tags":["北宋","风俗画","张择端"]}', 1);

INSERT INTO feedback (id, user_id, feedback_type, feedback_title, feedback_content, contact_info, status, processed_by, processed_at, process_result)
VALUES
(1, 4, 0, '搜索结果不够精准', '在搜索“青铜器”时，结果列表中文物年代跨度较大，希望可以增加按年代筛选功能。', 'student@example.com', 2, 2, '2025-01-12 09:00:00', '已记录需求，并计划在下个版本增加年代过滤条件。'),
(2, 5, 1, '文物描述疑似有误', '四羊方尊的出土地信息与我在教材中看到的不同，建议核实。', 'visitor@example.com', 2, 3, '2025-01-13 14:20:00', '已与专家核对，并在后台修正相关描述。'),
(3, 4, 2, '希望增加课堂模式', '建议增加“课堂演示模式”，可以一键隐藏敏感信息，只展示关键知识点。', 'student@example.com', 1, 1, '2025-01-14 10:30:00', '正在调研该需求的实现方式，计划在后续版本中试点。'),
(4, 3, 0, '专家审核界面交互建议', '审核列表中希望增加“最近提交”的排序方式，以便快速查看最新申请。', 'expert@example.com', 0, NULL, NULL, NULL);

INSERT INTO feedback_evaluation (feedback_id, rating, comment)
VALUES
(1, 5, '处理及时，功能优化后体验很好。'),
(2, 4, '说明较为详细，希望后续能开放修改记录查看功能。'),
(3, 5, '期待课堂模式尽快上线，对教学非常有帮助。');

SET FOREIGN_KEY_CHECKS = 1;
