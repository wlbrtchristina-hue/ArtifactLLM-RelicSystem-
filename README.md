# 大模型增强的文物资源知识管理系统（ArtifactLLM-RelicSystem）

面向博物馆文物数字化场景的**文物资源知识管理系统**（湖南大学软件工程课程团队项目）。系统以文物资源管理与检索为基础，融合 **Neo4j 知识图谱** 与 **DeepSeek 大模型智能问答**，提供可视化的文物知识探索与 AI 交互体验。

## 功能特性

- **文物资源管理**：文物信息的增删改查、分类管理、录入审核流程与统计
- **多维检索**：关键字检索与分类筛选，覆盖文物展示 / 检索 / 详情全流程
- **知识图谱可视化**：基于 Neo4j 存储文物、朝代、出土地点、工艺等实体及关联关系，前端以 ECharts 关系图交互式呈现
- **AI 智能问答助手**：集成 DeepSeek 大模型（deepseek-reasoner）实现文物知识问答，支持多轮对话与聊天历史记录
- **用户与权限**：图形验证码 + JWT 认证，基于角色的访问控制（RBAC），用户 / 角色 / 权限管理
- **反馈与评估**：用户反馈提交、评估与处理闭环

## 技术栈

| 层次 | 技术 |
| --- | --- |
| 后端 | Java 21、Spring Boot（Maven 多模块）、MyBatis-Plus、MySQL、Redis、Neo4j |
| AI | DeepSeek API（OkHttp 调用 chat/completions，带聊天历史） |
| 前端 | Vue 3、Element Plus、ECharts、Vue Router、Vuex、Vite |
| 安全 | JWT、图形验证码 |

## 模块结构

```
src/
├── ArtifactLLM_banker/          # Spring Boot 多模块后端（Maven）
│   ├── artifact-web             # 聚合 / 启动入口
│   ├── artifact-system          # 用户、角色、权限、反馈、消息模块
│   ├── artifact-relic           # 文物资源、类型、建模、审核、统计模块
│   ├── artifact-ai              # DeepSeek 智能问答模块（含聊天历史）
│   ├── artifact-knowledge       # Neo4j 知识图谱模块
│   ├── artifact-common          # 通用模块（JWT / Redis / 验证码 / 统一返回）
│   └── sql/                     # 数据库脚本
└── vue/                         # Vue 3 前端（Vite）
doc/
└── project/                     # 需求 / 设计 / 计划文档
    ├── 01-需求文档              # 需求规格说明书、用例文档、前景和范围文档
    ├── 02-设计文档              # UML 设计文档、数据库设计文档
    └── 03-计划文档              # 迭代开发计划
```

## 快速开始

**环境要求**：JDK 21、Maven 3.8+、MySQL 8、Redis、Neo4j、Node.js 16+

1. 初始化数据库：执行 `server/sql/` 下的建库脚本，或使用 `doc/source/` 中的 Python 脚本从原始数据生成导入脚本
2. 配置后端：复制各模块 `src/main/resources/` 下的 `application.properties.example` 为 `application.properties`，填写本地的 MySQL / Redis / Neo4j 连接信息与 DeepSeek API Key
3. 启动后端：Maven 编译并依次启动各业务模块
4. 启动前端：`cd src/vue && npm install && npm run dev`

## 说明

- 本仓库为**团队合作项目副本**，用于作品展示；配置中的 API Key、数据库密码等敏感信息不纳入版本控制（见各模块 `application.properties.example`）
- 原始文物数据集（JSONL / SQL，数百 MB）与团队开发过程文档未包含在本仓库中
- 知识图谱数据导入脚本见 `doc/source/import_neo4j_jsonl.py`
