# GITDB

> A command line tool to better organize, find, and inspect local git repositories.

### What problem does it solve?

If you have a lot of repositories on your local machine _(work, tutorial, experiments, hobby projects, etc..)_, in different folders, drives,
it can be hard to keep track, where everything is.

GITDB tries to solve this problem by scanning the computer for existing git repositories, and keeping a record about them in a sqlite database
in `~/.gitdb/repos.db`, and hooking into git commands, and updating the state of repositories.

You can find and list all the repositories on your machine, or your most recent ones, and `cd` into it.

### Install and initialization

- commands to classpath
- set GITDB_HOME environment variable with install path
- includes jre / kotlin native executable?
- for first run: `gitdb init`

### Commands:

- `gitdb init` - creates database, starts scan on all drives for directories containing `.git` folder.
 Creates and configures global templatedir `git config --global init.templatedir %userprofile%/.git-templates`.
 Adds a post-commit git hook to all repos with an `update <id>` call.
- `gitdb init quick` - same as above, but it only searches in parents of existing git repos (in db), not in drives. For quick testing.  
- `gitdb list` - lists .git repositories on your machine from it's database
- `gitdb cd <repo_id>` - change directory to repository identified by id (number / sha / project name)
- `gitdb favor <repo_id>` - mark repository as favorite
- `gitdb unfavor <repo_id>` - unmark repository as favorite
- `gitdb help` - prints help

 - `gitdb update <repo_id>` - updates git repo in the current directory - used by git hooks

### Screenshots
 <img src="images/gitdb_list.png" />
 <img src="images/gitdb_mix.png" />

### Similar projects

- https://github.com/MirkoLedda/git-summary
- https://github.com/lzakharov/gitls
