@echo off

java -Dlogging.level.root=WARN -jar %GITDB_HOME%/gitdb-0.0.1-SNAPSHOT.jar %1 %2

if %ERRORLEVEL% == 0 (
    REM 'gitdb cd <id>' command overrides run_cd.bat, after which run_cd.bat can be invoked,
    REM to change the directory (because from a java program this was not possible).
    if %1%==cd (
        @echo on
        run_cd.bat
        @echo off
    )
) else (
    echo "Errors encountered during execution.  Exited with status: %errorlevel%"
)

@echo on
