@echo off

java -Dlogging.level.root=WARN -jar D:/Java/gitdb_testinstall/gitdb-0.0.1-SNAPSHOT.jar %1 %2

REM 'ideadb cd <id>' command overrides run_cd.bat, after which this can be invoked, to change the directory, because from a java program this was not possible.
if %1%==cd run_cd.bat

@echo on
