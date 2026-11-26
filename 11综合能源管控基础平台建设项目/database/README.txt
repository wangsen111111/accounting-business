MySQL 初始化：
CREATE DATABASE energy_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

随后修改 src/main/resources/application-mysql.yml 的 username/password，启用 mysql profile 启动即可。
建表脚本位于 src/main/resources/schema.sql。
