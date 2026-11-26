# 智慧热力管网运行监测管理系统（专业升级版）

## 技术环境
JDK 1.8、Spring Boot 2.7.5、MySQL 5.7/8.0、Maven 3.6+、Thymeleaf、Spring MVC、JdbcTemplate。

## 本次新增功能
- 登录入口、会话登录、退出登录及未登录访问拦截。
- 巡检管理：选择换热站、登记巡检人员、结果和备注。
- 维修工单：故障登记、优先级、负责人、状态流转。
- 报警规则：供水温度、回水温度、压力、流量阈值维护。
- 综合报表：汇总管线、站点、报警、工单数据，支持浏览器打印。
- 侧边栏增加运维管理入口。

原有运行总览、实时监测、管道档案、换热站管理、报警管理、统计分析等功能保留。

## 启动步骤
1. 启动 MySQL 5.7/8.0。
2. 执行 `database/init.sql`，创建 `heat_pipeline` 数据库、表和演示数据。
3. 修改 `src/main/resources/application.properties` 中的数据库用户名和密码。
4. IDEA 打开项目根目录，等待 Maven 加载。
5. 运行 `com.demo.heat.HeatPipelineApplication`。
6. 访问 `http://localhost:8080`，系统会跳转到登录页。

## 演示账号
账号：`admin`；密码：`admin123`。

**安全提示：** 为便于本地演示，当前账号密码以明文形式存储。正式部署必须改为 BCrypt 等安全密码哈希，并补充角色权限、CSRF 防护、登录限流和审计日志；不要直接用于生产环境。

