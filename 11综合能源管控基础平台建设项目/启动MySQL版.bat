@echo off
chcp 65001 >nul
cd /d %~dp0
 echo 正在启动综合能源智慧管控平台 V4.0（MySQL）...
call mvn spring-boot:run -Dspring-boot.run.profiles=mysql
pause
