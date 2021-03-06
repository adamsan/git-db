@echo off

if "%1"=="ls" (
	gitdblist.bat
	goto End
)
if EXIST %GITDB_HOME%\custom_jre (
	%GITDB_HOME%\custom_jre\bin\java -jar %GITDB_HOME%/gitdb-0.0.2-SNAPSHOT.jar %1 %2
) else (
	java -jar %GITDB_HOME%/gitdb-0.0.2-SNAPSHOT.jar %1 %2
)

if %ERRORLEVEL% == 0 (
    REM 'gitdb cd <id>' command overrides run_cd.bat, after which this can be invoked, to change the directory, because from a java program this was not possible.
    if "%1%"=="cd" (
        @echo on
        run_cd.bat
        @echo off
    )
) else (
    echo "Errors encountered during execution.  Exited with status: %errorlevel%"
)

:End
@echo on
