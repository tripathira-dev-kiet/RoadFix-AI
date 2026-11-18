@echo off
title RoadFix AI - Frontend Only (No Database)
echo =========================================================
echo   WARNING: This opens Frontend WITHOUT the database!
echo   For full database mode, close this and run: run.bat
echo =========================================================
echo.
echo Opening RoadFix AI Frontend in browser...
start "" "%~dp0Frontend\index.html"
