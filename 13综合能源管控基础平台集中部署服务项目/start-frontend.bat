@echo off
setlocal
title GFECMP Frontend - port 5173

set "NODE_HOME=C:/Users/23847/.workbuddy/binaries/node/versions/22.22.2-6"
set "PATH=%NODE_HOME%;%PATH%"

cd /d "%~dp0frontend"
if errorlevel 1 goto :fail

where npm >nul 2>&1
if errorlevel 1 goto :nonpm

if exist "node_modules" (
    echo [1/2] node_modules found, skip install.
) else (
    echo [1/2] Installing npm dependencies, please wait ...
    call npm install --no-audit --no-fund
)

echo [2/2] Starting Vite dev server ...
echo       URL     : http://localhost:5173
echo       Account : admin / 123456
echo       Stop    : press Ctrl+C
echo.
call npm run dev
goto :theend

:nonpm
echo [ERROR] npm not found. Edit NODE_HOME at the top of this file.
goto :theend

:fail
echo [ERROR] Cannot enter folder: %~dp0frontend

:theend
echo.
echo Frontend stopped.
pause
