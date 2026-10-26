@echo off
cd /d "%~dp0"
if not exist "data" mkdir "data"
if "%APP_PASSWORD%"=="" set /p APP_PASSWORD=Defina a senha de acesso: 
call mvn spring-boot:run
pause
