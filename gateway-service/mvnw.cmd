@REM Maven Wrapper script for Windows CMD and PowerShell environments.
@REM Executes Java application using portable Maven download automatically.

@echo off
@setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set MAVEN_PROJECTBASEDIR=%DIRNAME%

if exist "%DIRNAME%\.mvn\wrapper\maven-wrapper.jar" (
    goto run
)

echo Downloading Maven Wrapper...
powershell -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; (New-Object System.Net.WebClient).DownloadFile('https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar', '%DIRNAME%\.mvn\wrapper\maven-wrapper.jar')"

:run
java -classpath "%DIRNAME%\.mvn\wrapper\maven-wrapper.jar" "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" org.apache.maven.wrapper.MavenWrapperMain %*
