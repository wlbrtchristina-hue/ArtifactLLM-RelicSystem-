-- 新的数据库构建脚本，基于原设计并根据 artifact 数据进行了调整

-- AI对话历史表
create table ai_chat_history
(
    id          bigint auto_increment comment 'ID'
        primary key,
    session_id  varchar(64)                        null comment '会话ID',
    user_id     bigint                             null comment '用户ID',
    user_input  text                               null comment '用户提问',
    ai_response longtext                           null comment 'AI回答',
    model       varchar(50)                        null comment '使用模型',
    create_time datetime default CURRENT_TIMESTAMP null comment '对话时间'
)
    comment 'AI对话历史表';

create index idx_session
    on ai_chat_history (session_id);

create index idx_user
    on ai_chat_history (user_id);

-- 审核记录表
create table audit
(
    audit_id      int auto_increment comment '审核ID'
        primary key,
    audit_status  varchar(20) default 'pending'         null comment '状态(pending/approved/rejected)',
    audit_type_id int                                   not null comment '审核类型ID',
    audit_data    json                                  not null comment '待审核数据快照(JSON)',
    created_by    bigint                                not null comment '申请人ID',
    auditor_id    bigint                                null comment '审核人ID',
    audit_time    datetime                              null comment '审核时间',
    reject_reason text                                  null comment '驳回原因',
    created_at    datetime    default CURRENT_TIMESTAMP null comment '申请时间',
    updated_at    datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '审核记录表';

create index idx_creator
    on audit (created_by);

create index idx_status
    on audit (audit_status);

-- 审核类型表
create table audit_type
(
    audit_type_id        int auto_increment comment '审核类型ID'
        primary key,
    audit_type_name      varchar(50) not null comment '类型名称(新增/修改/删除)',
    audit_content_fields text        null comment '审核内容描述'
)
    comment '审核类型表';

-- 文物表 (已修改)
create table cultural_relics
(
    relics_id        int auto_increment comment '文物ID'
        primary key,
    relics_name      varchar(50)                        null comment '文物名称',
    era              varchar(100)                       not null comment '年代',
    material         varchar(100)                       null comment '材质',
    relics_type_id   int                                not null comment '类型ID',
    discovery_site   varchar(200)                       null comment '出土地点',
    current_location varchar(200)                       null comment '现藏地点',
    description      text                               null comment '描述',
    custom_fields    json                               null comment '动态字段值(JSON)',
    created_by       bigint                             not null comment '创建者ID',
    created_at       datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updated_at       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_deleted       tinyint  default 0                 null comment '逻辑删除',
    cultural_code    varchar(64)                        null comment '文物号',
    external_id      varchar(64)                        null comment '来源ID(UUID)',
    detail_url       varchar(500)                       null comment '详情页URL'
)
    comment '文物表';

create index idx_creator
    on cultural_relics (created_by);

create index idx_type
    on cultural_relics (relics_type_id);

create index idx_cultural_code
    on cultural_relics (cultural_code);

create index idx_external_id
    on cultural_relics (external_id);

-- 实体关系表
create table entity_relations
(
    relation_id          int auto_increment comment '关系ID'
        primary key,
    relation_name        varchar(100)                       not null comment '关系名称',
    source_type          varchar(50)                        null comment '源实体类型',
    source_id            int                                not null comment '源实体ID',
    target_string        varchar(50)                        not null comment '目标实体名称/值',
    relation_description text                               null comment '描述',
    created_by           bigint                             not null comment '创建者ID',
    created_at           datetime default CURRENT_TIMESTAMP null comment '创建时间',
    is_deleted           tinyint  default 0                 null comment '逻辑删除'
)
    comment '实体关系表';

create index idx_source
    on entity_relations (source_id);

-- 用户反馈表
create table feedback
(
    id               bigint auto_increment comment '反馈ID'
        primary key,
    user_id          bigint                             not null comment '提交用户ID',
    feedback_type    int      default 0                 null comment '类型：0-建议，1-问题，2-需求',
    feedback_title   varchar(100)                       null comment '标题',
    feedback_content text                               null comment '内容',
    contact_info     varchar(100)                       null comment '联系方式',
    submitted_at     datetime default CURRENT_TIMESTAMP null comment '提交时间',
    status           int      default 0                 null comment '状态：0-待处理，1-处理中，2-已解决',
    processed_by     bigint                             null comment '处理人ID',
    processed_at     datetime                           null comment '处理时间',
    process_result   text                               null comment '处理回复'
)
    comment '用户反馈表';

create index idx_user
    on feedback (user_id);

-- 反馈评价表
create table feedback_evaluation
(
    id          bigint auto_increment comment '评价ID'
        primary key,
    feedback_id bigint                             not null comment '关联反馈ID',
    rating      int      default 5                 null comment '评分(1-5)',
    comment     varchar(500)                       null comment '评价内容',
    create_time datetime default CURRENT_TIMESTAMP null comment '评价时间',
    constraint uk_feedback
        unique (feedback_id)
)
    comment '反馈评价表';

-- 知识卡片缓存表
create table knowledge_card
(
    id          bigint auto_increment comment 'ID'
        primary key,
    relic_id    int                                not null comment '文物ID',
    card_json   json                               not null comment '完整知识卡片数据(JSON)',
    version     int      default 1                 null comment '版本号',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_relic
        unique (relic_id)
)
    comment '知识卡片缓存表';

-- 多模态资源表
create table relic_multi_mode
(
    resource_id      int auto_increment comment '资源ID'
        primary key,
    relics_id        int                                not null comment '文物ID',
    resource_type    varchar(50)                        not null comment '资源类型(image/video/3d_model)',
    resource_content text                               not null comment '资源地址或内容',
    created_by       bigint                             not null comment '创建者ID',
    created_at       datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updated_at       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_deleted       tinyint  default 0                 null comment '逻辑删除'
)
    comment '多模态资源表';

create index idx_relic
    on relic_multi_mode (relics_id);

-- 文物类型表
create table relics_type
(
    relics_type_id int auto_increment comment '文物类型ID'
        primary key,
    type_name      varchar(200)                       not null comment '类型名称',
    description    text                               null comment '类型描述',
    type_fields    json                               null comment '该类型的动态字段定义(JSON)',
    created_by     bigint                             not null comment '创建者ID',
    created_at     datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updated_at     datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_deleted     tinyint  default 0                 null comment '逻辑删除',
    constraint uk_type_name
        unique (type_name, is_deleted)
)
    comment '文物类型表';

-- 搜索记录表
create table search_record
(
    id          bigint auto_increment comment 'ID'
        primary key,
    keyword     varchar(100)                       not null comment '搜索关键词',
    user_id     bigint                             null comment '搜索用户',
    ip_address  varchar(50)                        null comment 'IP地址',
    create_time datetime default CURRENT_TIMESTAMP null comment '搜索时间'
)
    comment '搜索记录表';

create index idx_keyword
    on search_record (keyword);

-- 附件/文件表
create table sys_file
(
    id           bigint auto_increment comment '文件ID'
        primary key,
    file_name    varchar(200)                       not null comment '原始文件名',
    file_path    varchar(500)                       not null comment '文件存储路径/URL',
    file_size    bigint   default 0                 null comment '文件大小(字节)',
    file_type    varchar(50)                        null comment '文件类型(后缀)',
    content_type varchar(100)                       null comment 'MIME类型',
    uploaded_by  bigint                             null comment '上传人ID',
    create_time  datetime default CURRENT_TIMESTAMP null comment '上传时间'
)
    comment '附件/文件表';

-- 系统消息表
create table sys_message
(
    id          bigint auto_increment comment '消息ID'
        primary key,
    sender_id   bigint     default 0                 null comment '发送者ID(0代表系统)',
    receiver_id bigint                               not null comment '接收者ID',
    title       varchar(100)                         null comment '消息标题',
    content     text                                 not null comment '消息内容',
    type        int        default 0                 null comment '消息类型：0-系统通知，1-待办提醒',
    is_read     tinyint(1) default 0                 null comment '是否已读',
    read_time   datetime                             null comment '阅读时间',
    create_time datetime   default CURRENT_TIMESTAMP null comment '发送时间'
)
    comment '系统消息表';

create index idx_receiver
    on sys_message (receiver_id, is_read);

-- 操作日志表
create table sys_operation_log
(
    id          bigint auto_increment comment '日志ID'
        primary key,
    user_id     bigint                             null comment '操作用户ID',
    username    varchar(50)                        null comment '操作用户名',
    ip_address  varchar(50)                        null comment 'IP地址',
    module      varchar(50)                        null comment '操作模块',
    operation   varchar(50)                        null comment '操作类型',
    method      varchar(200)                       null comment '请求方法',
    params      text                               null comment '请求参数',
    result      text                               null comment '返回结果',
    status      int      default 0                 null comment '操作状态：0-成功，1-失败',
    error_msg   text                               null comment '错误信息',
    create_time datetime default CURRENT_TIMESTAMP null comment '操作时间'
)
    comment '操作日志表';

create index idx_time
    on sys_operation_log (create_time);

create index idx_user
    on sys_operation_log (user_id);

-- 权限表
create table sys_permission
(
    id          bigint auto_increment comment '权限ID'
        primary key,
    name        varchar(50)                        not null comment '权限名称',
    code        varchar(50)                        not null comment '权限标识',
    type        int      default 1                 null comment '类型：1-菜单，2-按钮',
    parent_id   bigint   default 0                 null comment '父权限ID',
    path        varchar(200)                       null comment '路由路径',
    component   varchar(200)                       null comment '前端组件',
    icon        varchar(50)                        null comment '图标',
    sort_order  int      default 0                 null comment '排序',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间'
)
    comment '权限表';

-- 角色表
create table sys_role
(
    id          bigint auto_increment comment '角色ID'
        primary key,
    name        varchar(50)                        not null comment '角色名称',
    code        varchar(50)                        not null comment '角色代码(如: admin, curator)',
    description varchar(200)                       null comment '描述',
    sort_order  int      default 0                 null comment '排序',
    status      int      default 0                 null comment '状态：0-正常，1-禁用',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_code
        unique (code)
)
    comment '角色表';

-- 角色权限关联表
create table sys_role_permission
(
    id            bigint auto_increment
        primary key,
    role_id       bigint not null comment '角色ID',
    permission_id bigint not null comment '权限ID',
    constraint uk_role_perm
        unique (role_id, permission_id)
)
    comment '角色权限关联表';

-- 用户表
create table sys_user
(
    id              bigint auto_increment comment '用户ID'
        primary key,
    username        varchar(50)                           not null comment '用户名',
    password        varchar(100)                          not null comment '密码',
    email           varchar(100)                          null comment '邮箱',
    phone           varchar(20)                           null comment '手机号',
    real_name       varchar(50)                           null comment '真实姓名',
    avatar          varchar(255)                          null comment '头像',
    role            varchar(50) default 'user'            null comment '角色标识(冗余字段)',
    status          int         default 0                 null comment '状态：0-正常，1-禁用',
    last_login_time datetime                              null comment '最后登录时间',
    create_time     datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    update_time     datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_username
        unique (username)
)
    comment '用户表';

-- 用户角色关联表
create table sys_user_role
(
    id      bigint auto_increment
        primary key,
    user_id bigint not null comment '用户ID',
    role_id bigint not null comment '角色ID',
    constraint uk_user_role
        unique (user_id, role_id)
)
    comment '用户角色关联表';

-- 元模型主表
create table meta_model
(
    id          bigint auto_increment comment '模型ID'
        primary key,
    name        varchar(100)                       not null comment '模型名称',
    description varchar(500)                       null comment '模型描述',
    creator_id  bigint                             not null comment '创建者ID',
    status      int      default 0                 null comment '状态：0-草稿，1-已发布',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '元模型主表';

create index idx_model_creator
    on meta_model (creator_id);

-- 实体定义表
create table meta_entity_def
(
    id          bigint auto_increment comment '实体定义ID'
        primary key,
    model_id    bigint                             not null comment '所属模型ID',
    name        varchar(100)                       not null comment '实体名称',
    code        varchar(100)                       null comment '实体编码',
    description varchar(500)                       null comment '描述',
    x_pos       int      default 0                 null comment '画布X坐标',
    y_pos       int      default 0                 null comment '画布Y坐标',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间'
)
    comment '实体定义表';

create index idx_entity_model
    on meta_entity_def (model_id);

-- 属性定义表
create table meta_attr_def
(
    id            bigint auto_increment comment '属性定义ID'
        primary key,
    entity_def_id bigint                             not null comment '所属实体定义ID',
    name          varchar(100)                       not null comment '属性名称',
    code          varchar(100)                       null comment '属性编码',
    type          varchar(50)                        not null comment '数据类型(text/number/date/boolean/select/file)',
    required      tinyint(1) default 0               null comment '是否必填',
    description   varchar(500)                       null comment '描述',
    options       json                               null comment '选项列表(JSON)',
    create_time   datetime default CURRENT_TIMESTAMP null comment '创建时间'
)
    comment '属性定义表';

create index idx_attr_entity
    on meta_attr_def (entity_def_id);

-- 关系定义表
create table meta_relation_def
(
    id               bigint auto_increment comment '关系定义ID'
        primary key,
    model_id         bigint                             not null comment '所属模型ID',
    name             varchar(100)                       not null comment '关系名称',
    type             varchar(50)                        default 'one-to-many' null comment '关系类型(one-to-one/one-to-many/many-to-one/many-to-many)',
    source_entity_id bigint                             not null comment '源实体ID',
    target_entity_id bigint                             not null comment '目标实体ID',
    description      varchar(500)                       null comment '描述',
    create_time      datetime default CURRENT_TIMESTAMP null comment '创建时间'
)
    comment '关系定义表';

create index idx_rel_model
    on meta_relation_def (model_id);

-- 实体实例数据表
create table instance_data
(
    id            bigint auto_increment comment '实例ID'
        primary key,
    model_id      bigint                             not null comment '所属模型ID',
    entity_def_id bigint                             not null comment '所属实体定义ID',
    name          varchar(200)                       not null comment '实例名称(显示用)',
    data_json     json                               not null comment '实例数据(JSON)',
    creator_id    bigint                             not null comment '创建者ID',
    create_time   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '实体实例数据表';

create index idx_instance_model
    on instance_data (model_id);

create index idx_instance_entity
    on instance_data (entity_def_id);
