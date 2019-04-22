@echo off
call setenv.bat

SET JAVA_MARK=CronTabRunner
SET JAVA_OPTS= -Xms128m 
SET JAVA_OPTS=%JAVA_OPTS% -Xmx1024m
rem SET JAVA_OPTS=%JAVA_OPTS% -Dfile.encoding=UTF-8
SET JAVA_OPTS=%JAVA_OPTS% -Dfile.encoding=GBK
SET JAVA_OPTS=%JAVA_OPTS% -DjavaMark=%JAVA_MARK%
SET JAVA_CONF=./etc
SET JAVA_LIBPATH=./lib
SET JAVA_CLASSPATH=./classes;./bin;%JAVA_CONF%
SET JAVA_MAINCLASS=com.amarsoft.business.CronTabRunner
SET JAVA_EXEC=%JAVA_HOME%/bin/java

rem mk logs dir
if NOT EXIST "./logs" MKDIR "logs"
rem init TEMP_CLASSPATH
SET TEMP_CLASSPATH=
rem new setclasspath.bat
echo SET TEMP_CLASSPATH=%%TEMP_CLASSPATH%%;%%1> setclasspath.bat

FOR  %%i IN (%JAVA_LIBPATH%/*.jar) DO (
CALL setclasspath.bat %JAVA_LIBPATH%/%%i
)

SET JAVA_CLASSPATH=%JAVA_CLASSPATH%;%TEMP_CLASSPATH%
rem delete setclasspath.bat
DEL setclasspath.bat

rem Display our environment
echo ===============================================================================  
echo Bootstrap Environment 
echo.  
echo JAVA_CLASSPATH =  %JAVA_CLASSPATH%
echo JAVA_CONF      =  %JAVA_CONF%  
echo JAVA_OPTS      =  %JAVA_OPTS%  
echo JAVA_HOME      =  %JAVA_HOME%  
echo JAVA           =  %JAVA_EXEC%  
echo.  
%JAVA_EXEC% -version
echo.  
echo ===============================================================================  
echo.  
  
%JAVA_EXEC% %JAVA_OPTS%  -classpath %JAVA_CLASSPATH% %JAVA_MAINCLASS% --config packdb.xml

echo run finished
PAUSE