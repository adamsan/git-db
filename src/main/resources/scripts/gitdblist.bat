@echo off

sqlite3  -column -header  %userprofile%/.git-db/.repos.db "select id, name, path, iif(favorite, '*', '') as favorite,  date(last_committed/1000, 'unixepoch', 'localtime') as last_commit_date, (strftime('%%s','now') - last_committed/1000)/60/60/24 as days_ago, iif(has_remote, '*', '') as has_remote from repo "

@echo on