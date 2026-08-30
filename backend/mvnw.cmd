@echo off
rem ============================================================
rem  Yanyan Backend Maven Wrapper (mvnw.cmd)
rem ============================================================
setlocal

set EXEC_DIR=%CD%
set WDIR=%EXEC_DIR%
:findBaseDir
if exist "%WDIR%\.mvn" goto baseDirFound
cd ..
set WDIR=%CD%
goto findBaseDir

:baseDirFound
set MAVEN_PROJECTBASEDIR=%WDIR%
cd "%EXEC_DIR%"

set MAVEN_WRAPPER_JAR=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar
if not exist "%MAVEN_WRAPPER_JAR%" (
  echo maven-wrapper.jar not found. Generate it via: mvn -N wrapper:wrapper
  exit /b 1
)

if defined JAVA_HOME (
  set WRAPPER_CMD="%JAVA_HOME%\bin\java.exe"
) else (
  set WRAPPER_CMD=java
)

%WRAPPER_CMD% %MAVEN_OPTS% -Dmaven.multiModuleProjectDirectory="%MAVEN_PROJECTBASEDIR%" -classpath "%MAVEN_WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %*
endlocal