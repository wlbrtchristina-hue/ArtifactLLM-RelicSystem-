-- ------------------------------------------------------------
-- script_3_modeling.sql
-- 仅包含“数据建模”相关的新表结构
-- 可在已经执行过 script_2.sql 的数据库中独立执行
-- ------------------------------------------------------------

-- 1. 元模型主表
create table if not exists meta_model
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


-- 2. 实体定义表
create table if not exists meta_entity_def
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


-- 3. 属性定义表
create table if not exists meta_attr_def
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


-- 4. 关系定义表
create table if not exists meta_relation_def
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


-- 5. 实体实例数据表
create table if not exists instance_data
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

