@echo off
setlocal
set MVNW_CMD=.mvn\wrapper\maven-wrapper.jar
IF NOT EXIST "%MVNW_CMD%" (
  echo Error: %MVNW_CMD% not found. Please run 'mvn -N io.takari:maven:wrapper'
  exit /B 1
)
java -jar %MVNW_CMD% %*
