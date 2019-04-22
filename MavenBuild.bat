SET JAVA_HOME=%cd%/jdk
call setenv.bat

%MAVEN_HOME%/bin/mvn  -f %POM_FILE%  compile

PAUSE
