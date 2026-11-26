@echo off
setlocal
title GFECMP Backend - port 8080

set "JAVA_HOME=C:/Users/23847/.workbuddy/runtimes/jdk-17.0.13+11"
set "MAVEN_HOME=C:/Users/23847/.workbuddy/runtimes/apache-maven-3.9.9"
set "PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%"

cd /d "%~dp0backend"
if errorlevel 1 goto :fail
if not exist "%JAVA_HOME%\bin\java.exe" goto :nojava

if exist "target\gfecmp-backend.jar" (
    echo [1/1] Starting jar : target\gfecmp-backend.jar
    echo       API      : http://localhost:8080/api/health
    echo       Stop     : press Ctrl+C
    echo.
    "%JAVA_HOME%\bin\java.exe" -Dfile.encoding=UTF-8 -jar "target\gfecmp-backend.jar" --server.port=8080
) else (
    echo [1/1] Jar not found, running with Maven ...
    echo.
    call "%MAVEN_HOME%\bin\mvn.cmd" -B spring-boot:run -Dspring-boot.run.arguments=--server.port=8080
)
goto :theend

:nojava
echo [ERROR] java.exe not found: %JAVA_HOME%\bin\java.exe
echo         Edit JAVA_HOME at the top of this file.
goto :theend

:fail
echo [ERROR] Cannot enter folder: %~dp0backend

:theend
echo.
echo Backend stopped.
pause
