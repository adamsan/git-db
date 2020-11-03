# GITDB

> A command line tool to better organize, find, and inspect local git repositories.

###What problem does it solve?

If you have a lot of repositories on your local machine _(work, tutorial, experiments, hobby projects, etc..)_, in different folders, drives,
it can be hard to keep track, where everything is.

GITDB tries to solve this problem by scanning the computer for existing git repositories, and keeping a record about them in a sqlite database
in `~/.gitdb/repos.db`, and hooking into git commands, and updating the state of repositories.

You can find and list all the repositories on your machine, or your most recent ones, and `cd` into it.

###Install and initialization

- commands to classpath
- includes jre / kotlin native executable?
- for first run: `gitdb init`

###Commands:

- `gitdb init` - creates database, starts scan on all drives for directories containing `.git` folder,
creates and configures global templatedir `git config --global init.templatedir %userprofile%/.git-templates`
- `gitdb list` - lists .git repositories on your machine from it's database
- `gitdb cd <repo_id>` - change directory to repository identified by id (number / sha / project name)
- `gitdb favor <repo_id>` - mark repository as favorite
- `gitdb unfavor <repo_id>` - unmark repository as favorite
- `gitdb help` - prints help

 - `gitdb update` - updates git repo in the current directory - used by git hooks

###Similar projects

- https://github.com/MirkoLedda/git-summary/blob/master/git-summary
- https://github.com/lzakharov/gitls
