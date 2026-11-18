@echo off
title RoadFix AI - Smart Municipal Platform (MongoDB Edition)
color 0b
echo =====================================================================
echo       RoadFix AI - Smart Municipal Platform (MongoDB Edition)
echo =====================================================================
echo.
echo [1/3] Ensuring Port 8080 is clear...
taskkill /F /IM java.exe >nul 2>&1
ping 127.0.0.1 -n 2 >nul

echo [2/3] Setting Java Environment...
if exist "C:\Program Files\Java\jdk-24\bin\java.exe" (
    set "JAVA_HOME=C:\Program Files\Java\jdk-24"
    set "PATH=C:\Program Files\Java\jdk-24\bin;%PATH%"
) else if exist "C:\Program Files\Java\jdk-21\bin\java.exe" (
    set "JAVA_HOME=C:\Program Files\Java\jdk-21"
    set "PATH=C:\Program Files\Java\jdk-21\bin;%PATH%"
)

echo [3/3] Starting Spring Boot Server on http://localhost:8080 ...
echo [INFO] Loading configuration from .env (MongoDB: roadfix_db)...
echo.

cd /d "%~dp0Backend"

REM Open web browser after 4 seconds
start "" cmd /c "ping 127.0.0.1 -n 4 >nul & start http://localhost:8080/"

REM Start the application
call .\mvnw.cmd spring-boot:run

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Spring Boot could not start. Please check the logs above.
    pause
)
