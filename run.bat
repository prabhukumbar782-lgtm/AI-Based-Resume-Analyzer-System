@echo off
echo ==========================================
echo   AI-Based Resume Analyzer System
echo ==========================================
echo.
java -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Java is not installed or not in PATH.
    echo Please install Java 11+ from https://adoptium.net
    pause
    exit /b 1
)
echo Starting Resume Analyzer...
java -jar ResumeAnalyzer.jar
pause
