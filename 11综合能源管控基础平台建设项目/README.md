# 综合能源智慧管控平台 V4.0（重新构建版）

本项目针对此前版本登录后首页 500 / Thymeleaf 模板解析异常的问题重新搭建，未沿用旧版模板片段。页面使用单一 dashboard 模板和明确的 Controller 模型变量，避免碎片模板引用、缺失变量导致的解析问题。

## 技术栈
- Java 8
- Spring Boot 2.7.18
- Spring MVC + Thymeleaf
- Spring JDBC
- MySQL 8（mysql profile）
- H2（profile，开箱可启动，不要求先安装 MySQL）
- Maven

## 默认启动（建议先验证页面）
1. 使用 JDK 8 或更高版本、Maven 3.6+。
2. IDEA -> Open -> 选择本目录的 `pom.xml` / 项目根目录。
3. 等待 Maven 下载依赖。
4. 运行 `src/main/java/com/energy/platform/PlatformApplication.java`。
5. 访问 http://localhost:8080/login
6. 登录：`admin / admin123`，也可使用 `operator / operator123`、`viewer / viewer123`。

默认使用内存 H2 数据库，启动时自动建表并初始化演示数据。重启后演示数据会恢复。

## 切换到 MySQL
1. 创建数据库：
   ```sql
   CREATE DATABASE energy_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
   ```
2. 修改 `src/main/resources/application-mysql.yml` 中的 username/password。
3. IDEA 运行配置的 Active profiles 填写 `mysql`；或命令行运行：
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=mysql
   ```
4. 首次启动自动执行 `schema.sql` 建表和初始化演示数据（表为空时）。

## 功能模块
- 综合能源驾驶舱：能耗指标、能源结构、趋势示意、设备在线状态、告警摘要
- 能源设备台账：电/水/气设备清单、新增设备
- 实时监测中心、能源统计分析、数据采集与治理：设备读数、数据质量示意
- 告警管理：告警列表、确认处理
- 运维工单：新增工单、完成工单
- 能源报表：设备数据 CSV 导出
- 园区管理、能源拓扑、双碳分析、能源价格、系统集成、审计、用户权限、系统设置等业务入口

## 常见问题
- 首页 500：先确认访问 `/login` 后登录；查看 IDEA 控制台最早出现的 `Caused by`。本版不依赖旧版 `fragments.html`。
- MySQL 连接失败：先用默认 H2 配置启动验证页面，再启用 mysql profile 并核对数据库服务、端口、账号密码。
- 端口占用：将 `application.yml` 的 `server.port` 改为其他端口。
